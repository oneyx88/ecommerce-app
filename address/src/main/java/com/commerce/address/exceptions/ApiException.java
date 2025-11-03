package com.commerce.address.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * @author Yixi Wan
 * @date 2025/10/21 16:35
 * @package com.commerce.ecommapp.exceptions
 * <p>
 * Description:
 */
@Getter
public class ApiException extends RuntimeException {
    private final HttpStatus status;

    public ApiException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}