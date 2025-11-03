package com.commerce.product.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Yixi Wan
 * @date 2025/10/21 20:03
 * @package com.commerce.ecommapp.config
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
