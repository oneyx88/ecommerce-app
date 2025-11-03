package com.commerce.user.service;

import com.commerce.user.dto.MessageResponse;
import com.commerce.user.dto.SignupRequest;
import com.commerce.user.dto.UserInfoResponse;
import com.commerce.user.model.AppRole;

/**
 * @author Yixi Wan
 * @date 2025/10/28 22:11
 * @package com.commerce.user.service
 * <p>
 * Description:
 */
public interface UserService {
    MessageResponse registerUser(SignupRequest request, AppRole role);

    UserInfoResponse getUserInfo(String keycloakId);

    boolean isUserActive(String keycloakId);
}
