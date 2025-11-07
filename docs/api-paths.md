# API 路径版本化与网关策略

为统一各微服务的接口前缀并加强权限控制，现已将所有服务的默认前缀升级为`/api/v1`，并对管理与销售相关端点增加`/admin`与`/seller`前缀。

## 统一前缀
- 统一基准：`/api/v1`
- 示例：
  - 用户：`POST /api/v1/signup/users`、`GET /api/v1/user`
  - 商品：`GET /api/v1/products`、`GET /api/v1/product/{id}`
  - 类目：`GET /api/v1/categories`
  - 库存：`GET /api/v1/inventory/product/{productId}`、`POST /api/v1/inventory`
  - 订单：`POST /api/v1/orders`
  - 支付：`POST /api/v1/payments`、`GET /api/v1/payments/{paymentId}`
  - 购物车：`GET /api/v1/carts/users/cart`、`DELETE /api/v1/cart/clear`
  - 地址：`GET /api/v1/addresses`

## 管理/销售端点
- 管理端：`/api/v1/admin/**`
  - 类目：`POST /api/v1/admin/category`、`PUT /api/v1/admin/category/{id}`、`DELETE /api/v1/admin/category/{id}`
  - 商品：`POST /api/v1/admin/categories/{categoryId}/product`、`PUT /api/v1/admin/product/{productId}`、`PUT /api/v1/admin/product/{productId}/image`、`DELETE /api/v1/admin/product/{productId}`
- 销售端：`/api/v1/seller/**`（如后续开放卖家权限可复用该前缀）

## 网关与安全
- 网关已路由并匹配上述版本化前缀与资源名。
- 安全策略：
  - `/api/v1/admin/**` 需`ADMIN`角色
  - `/api/v1/seller/**` 需`SELLER`角色
  - 其余业务端点需`USER`角色或`permitAll`（如登录、OAuth等）

## Feign Client
- 所有Feign客户端已统一至`/api/v1`并校正端口：
  - `product-service`：`http://localhost:8082` + `path=/api/v1`
  - `inventory-service`：`http://localhost:8084` + `path=/api/v1`
  - `cart-service`：`http://localhost:8086` + `path=/api/v1`

如发现仍使用`/api`或`/public`的旧路径，请统一替换为上述版本化路径。