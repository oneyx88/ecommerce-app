package com.commerce.inventory.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Yixi Wan
 * @date 2025/11/3 11:25
 * @package com.commerce.inventory.config
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
