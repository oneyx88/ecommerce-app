package com.commerce.payment.config;

import com.commerce.payment.dto.PaymentRequest;
import com.commerce.payment.model.Payment;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Yixi Wan
 * @date 2025/11/3 17:19
 * @package com.commerce.payment.config
 * <p>
 * Description:
 */
@Configuration
class MapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT);
        mapper.typeMap(PaymentRequest.class, Payment.class)
                .addMappings(m -> m.skip(Payment::setPaymentId));
        return mapper;
    }
}
