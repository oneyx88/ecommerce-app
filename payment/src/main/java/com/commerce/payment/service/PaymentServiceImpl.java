package com.commerce.payment.service;

import com.commerce.payment.dto.PaymentRequest;
import com.commerce.payment.dto.PaymentResponse;
import com.commerce.payment.exceptions.ResourceNotFoundException;
import com.commerce.payment.kafka.PaymentEvent;
import com.commerce.payment.model.Payment;
import com.commerce.payment.repository.PaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;

/**
 * @author Yixi Wan
 * @date 2025/11/3 17:14
 * @package com.commerce.payment.service
 * <p>
 * Description:
 */
@Service
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private KafkaTemplate<String, PaymentEvent> kafkaTemplate;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    @Transactional
    public PaymentResponse createPayment(PaymentRequest request) {
        // 1️⃣ 创建 Payment 实体
        Payment payment = modelMapper.map(request, Payment.class);
        payment.setPaymentStatus("INITIATED");
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());

        // 2️⃣ 保存数据库
        Payment saved = paymentRepository.save(payment);

        // 3️⃣ 注册事务提交后发送消息
        registerPaymentEventAfterCommit(
                buildPaymentEvent(saved, "INITIATED"),
                "payment-initiated"
        );

        // 4️⃣ 返回响应
        return modelMapper.map(saved, PaymentResponse.class);
    }

    @Override
    @Transactional
    public PaymentResponse updatePaymentStatus(Long paymentId, String status, String message) {
        // 1️⃣ 查找并更新状态
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "PaymentId", paymentId));

        payment.setPaymentStatus(status);
        payment.setPgResponseMessage(message);
        payment.setUpdatedAt(LocalDateTime.now());
        Payment updated = paymentRepository.save(payment);

        // 2️⃣ 注册事务提交后发送消息
        registerPaymentEventAfterCommit(
                buildPaymentEvent(updated, status),
                "payment-status-updated"
        );

        // 3️⃣ 返回响应
        return modelMapper.map(updated, PaymentResponse.class);
    }

    @Override
    public PaymentResponse getPaymentByPaymentId(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "PaymentId", paymentId));
        return modelMapper.map(payment, PaymentResponse.class);
    }

    // ---------------------------------------------------------------
    // 🔹 以下为提取出的通用方法
    // ---------------------------------------------------------------

    /**
     * ✅ 在事务提交后再发送 Kafka 消息
     */
    private void registerPaymentEventAfterCommit(PaymentEvent event, String topic) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                sendPaymentMessage(topic, event);
            }
        });
    }

    /**
     * ✅ 构建 PaymentEvent（避免重复代码）
     */
    private PaymentEvent buildPaymentEvent(Payment payment, String status) {
        return new PaymentEvent(
                payment.getOrderId(),
                payment.getPaymentId(),
                status,
                payment.getAmount(),
                LocalDateTime.now()
        );
    }

    /**
     * ✅ 实际发送 Kafka 消息（包含异常日志）
     */
    private void sendPaymentMessage(String topic, PaymentEvent event) {
        try {
            kafkaTemplate.send(topic, event);
            log.info("[Kafka] PaymentEvent sent → topic=" + topic + ", orderId=" + event.getOrderId());
        } catch (Exception e) {
            log.error("Failed to send PaymentEvent to topic=" + topic + ": " + e.getMessage(), e);
        }
    }
}

