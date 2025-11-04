package com.commerce.payment.service;

import com.commerce.payment.dto.PaymentRequest;
import com.commerce.payment.dto.PaymentResponse;
import jakarta.validation.Valid;

/**
 * @author Yixi Wan
 * @date 2025/11/3 17:12
 * @package com.commerce.payment.service
 * <p>
 * Description:
 */
public interface PaymentService {
    PaymentResponse createPayment(@Valid PaymentRequest request);

    PaymentResponse getPaymentByPaymentId(Long paymentId);

    PaymentResponse updatePaymentStatus(Long paymentId, String status, String message);

}
