package com.commerce.order.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Yixi Wan
 * @date 2025/11/2 23:30
 * @package com.commerce.order.config
 * <p>
 * Description:
 */
@Configuration
class MapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
