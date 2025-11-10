package com.commerce.payment.controller;

import com.commerce.payment.dto.PaymentRequest;
import com.commerce.payment.dto.PaymentResponse;
import com.commerce.payment.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

/**
 * @author Yixi Wan
 * @date 2025/11/3 17:11
 * @package com.commerce.payment.controller
 * <p>
 * Description:
 */
@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    /** 1️⃣ 创建支付（INITIATED） */
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PaymentResponse> createPayment(@Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.created(URI.create("/api/v1/payments/"+request.getOrderId())).body(paymentService.createPayment(request));
    }

    /** 2️⃣ 查询支付详情 */
    @GetMapping("/{paymentId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PaymentResponse> getPaymentByPaymentId(@PathVariable Long paymentId) {
        return ResponseEntity.ok(paymentService.getPaymentByPaymentId(paymentId));
    }

    /** 3️⃣ 更新支付状态（由网关或异步事件触发） */
    @PutMapping("/{paymentId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaymentResponse> updatePaymentStatus(
            @PathVariable Long paymentId,
            @RequestParam String status,
            @RequestParam(required = false) String message) {
        return ResponseEntity.ok(paymentService.updatePaymentStatus(paymentId, status, message));
    }
}
