package com.commerce.user.controller;

import com.commerce.user.dto.MessageResponse;
import com.commerce.user.dto.SignupRequest;
import com.commerce.user.dto.UserInfoResponse;
import com.commerce.user.model.AppRole;
import com.commerce.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author Yixi Wan
 * @date 2025/10/28 21:33
 * @package com.commerce.user.controller
 * <p>
 * Description:
 */
@RestController
@RequestMapping("/api")
class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/signup/users")
    public ResponseEntity<MessageResponse> registerUser(@Valid @RequestBody SignupRequest request) {
        return ResponseEntity.ok(userService.registerUser(request, AppRole.ROLE_USER));
    }

    @PostMapping("/signup/sellers")
    public ResponseEntity<MessageResponse> registerSeller(@Valid @RequestBody SignupRequest request) {
        return ResponseEntity.ok(userService.registerUser(request, AppRole.ROLE_SELLER));
    }

    @GetMapping("/user")
    public ResponseEntity<UserInfoResponse> getUserInfo(@RequestHeader("X-User-Id") String keycloakId) {

        UserInfoResponse userInfo = userService.getUserInfo(keycloakId);
        return ResponseEntity.ok(userInfo);
    }

    @GetMapping("/active/{keycloakId}")
    public ResponseEntity<Boolean> isUserActive(@PathVariable String keycloakId) {
        boolean active = userService.isUserActive(keycloakId);
        return ResponseEntity.ok(active);
    }

}
