package com.commerce.order.exceptions;

import lombok.Getter;

/**
 * @author Yixi Wan
 * @date 2025/10/21 15:57
 * @package com.commerce.ecommapp.exceptions
 * <p>
 * Description:
 */
@Getter
public class ResourceNotFoundException extends RuntimeException {

    private String resourceName;
    private String fieldName;
    private Object fieldValue;

    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s : '%s'", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }

}
