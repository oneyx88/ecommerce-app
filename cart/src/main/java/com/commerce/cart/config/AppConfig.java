package com.commerce.cart.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Yixi Wan
 * @date 2025/10/29 17:26
 * @package com.commerce.order.config
 * <p>
 * Description:
 */
@Configuration
class AppConfig {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

}
