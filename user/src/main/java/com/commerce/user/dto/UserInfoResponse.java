package com.commerce.user.dto;

import com.commerce.user.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * @author Yixi Wan
 * @date 2025/10/29 15:08
 * @package com.commerce.user.dto
 * <p>
 * Description:
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponse {

    private Long userId;
    private String keycloakId;

    private String username;
    private String firstName;
    private String lastName;
    private String email;

    private Boolean enabled;
    private Boolean deleted;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Set<Role> roles;              // 从 Role.name 映射
    private List<AddressResponse> addresses; // 从 Address 实体映射

    private String message; // 可选，用于业务提示
}
