package com.commerce.user.service;

import com.commerce.user.dto.AddressResponse;
import com.commerce.user.dto.MessageResponse;
import com.commerce.user.dto.SignupRequest;
import com.commerce.user.dto.UserInfoResponse;
import com.commerce.user.exceptions.ApiException;
import com.commerce.user.exceptions.ResourceNotFoundException;
import com.commerce.user.model.AppRole;
import com.commerce.user.model.Role;
import com.commerce.user.model.User;
import com.commerce.user.repository.RoleRepository;
import com.commerce.user.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Yixi Wan
 * @date 2025/10/28 22:12
 * @package com.commerce.user.service
 * <p>
 * Description:
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private KeycloakUserService keycloakUserService;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Value("${keycloak.realm}")
    private String realm;

    @Override
    @Transactional
    public MessageResponse registerUser(SignupRequest request, AppRole role) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ApiException("Username " + request.getUsername() + " is already taken!", HttpStatus.BAD_REQUEST);
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ApiException("Email " + request.getEmail() + " is already taken!", HttpStatus.BAD_REQUEST);
        }

        // 创建keycloak user
        String keycloakUserId = keycloakUserService.createUser(request); // 回调keycloak id
        keycloakUserService.assignRealmRole(keycloakUserId, String.valueOf(role));

        // 查找数据库role
        Role dbRole = roleRepository.findByRoleName(role)
                .orElseThrow(() -> new ApiException("Role " + role + " not found in database", HttpStatus.INTERNAL_SERVER_ERROR));

        // 构建user实体
        User user = modelMapper.map(request, User.class);
        user.setKeycloakId(keycloakUserId);
        user.setRoles(Set.of(dbRole));
        user.setCreatedAt(LocalDateTime.now());

        userRepository.save(user);

        return new MessageResponse("User registered successfully!");
    }

    @Override
    public UserInfoResponse getUserInfo(String keycloakId) {
        // 当数据库中不存在该 Keycloak 用户时，直接抛出异常
        User user = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "keycloakId", keycloakId));
        return modelMapper.map(user, UserInfoResponse.class);
    }

    @Override
    public boolean isUserActive(String keycloakId) {
        return userRepository.existsByKeycloakIdAndEnabledTrueAndDeletedFalse(keycloakId);
    }
}
