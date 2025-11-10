package com.commerce.user.controller;

import com.commerce.user.dto.MessageResponse;
import com.commerce.user.dto.SignupRequest;
import com.commerce.user.dto.UserInfoResponse;
import com.commerce.user.model.AppRole;
import com.commerce.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author Yixi Wan
 * @date 2025/10/28 21:33
 * @package com.commerce.user.controller
 * <p>
 * Description:
 */
@RestController
@RequestMapping("/api/v1/users")
class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    @PreAuthorize("permitAll()")
    public ResponseEntity<MessageResponse> registerUser(@Valid @RequestBody SignupRequest request) {
        return ResponseEntity.ok(userService.registerUser(request, AppRole.USER));
    }

    @PostMapping("/signup/sellers")
    @PreAuthorize("permitAll()")
    public ResponseEntity<MessageResponse> registerSeller(@Valid @RequestBody SignupRequest request) {
        return ResponseEntity.ok(userService.registerUser(request, AppRole.SELLER));
    }

    @GetMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<UserInfoResponse> getUserInfo(@RequestHeader("X-User-Id") String keycloakId) {
        UserInfoResponse userInfo = userService.getUserInfo(keycloakId);
        return ResponseEntity.ok(userInfo);
    }


}