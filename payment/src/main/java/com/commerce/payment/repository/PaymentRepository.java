package com.commerce.payment.repository;

import com.commerce.payment.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Yixi Wan
 * @date 2025/11/3 17:16
 * @package com.commerce.payment.repository
 * <p>
 * Description:
 */
public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
