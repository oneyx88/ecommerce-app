package com.commerce.product.exceptions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Yixi Wan
 * @date 2025/10/21 22:36
 * @package com.commerce.ecommapp.exceptions
 * <p>
 * Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiResponse<T> {
    private long timestamp;      // 响应时间戳
    private int statusCode;      // 状态码（HTTP code 或业务码）
    private String message;      // 响应信息
//    private T data;              // 返回数据

}
