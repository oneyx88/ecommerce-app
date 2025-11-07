# 本地访问 Keycloak 指南（Docker Desktop + Kubernetes）

为在本地通过 `https://keycloak.local` 访问 Keycloak 管理界面，请按以下步骤完成 Ingress、域名解析与 TLS 配置。

## 1. 安装 Ingress-NGINX 控制器

Docker Desktop 的内置 Kubernetes 不自带 Ingress 控制器，需要手动安装：

```bash
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/main/deploy/static/provider/cloud/deploy.yaml

# 等待控制器就绪
kubectl -n ingress-nginx rollout status deploy/ingress-nginx-controller
```

> 如果你已使用其他方式安装了 Ingress-NGINX，请确认其 `ingressClass` 为 `nginx`。

## 2. hosts 域名映射

将 `keycloak.local`、`api.local`、`redis.local` 映射到本机，以便浏览器能找到 Ingress：

```bash
sudo sh -c 'echo "127.0.0.1 keycloak.local api.local redis.local" >> /etc/hosts'
```

如果你使用的是非本机 IP（例如 Docker Desktop 的集群 LB IP），请将上面的 `127.0.0.1` 替换为实际 IP。

## 3. 创建 TLS 证书与 Secret

Ingress 中启用了 TLS（`secretName: ecommerce-tls`）。在本地开发环境建议使用 `mkcert` 生成受信任的本地证书：

```bash
brew install mkcert nss # macOS 安装 mkcert（如已安装可跳过）
mkcert -install

# 为多个主机生成证书
mkcert keycloak.local api.local redis.local

# 生成文件类似：
# - keycloak.local+2.pem
# - keycloak.local+2-key.pem

# 创建或更新 Kubernetes TLS Secret
kubectl create secret tls ecommerce-tls \
  --namespace default \
  --cert=keycloak.local+2.pem \
  --key=keycloak.local+2-key.pem \
  --dry-run=client -o yaml | kubectl apply -f -
```

> 如果你不使用 `mkcert`，也可以用 `openssl` 生成自签名证书，但浏览器可能提示不受信任。

## 4. 应用 Ingress 与服务

确保已部署 Keycloak 与 Ingress（本仓库已提供清单文件）：

```bash
kubectl apply -f k8s/services/keycloak/
kubectl apply -f k8s/ingress/ingress.yml
```

## 5. 验证访问

- 浏览器访问 `https://keycloak.local/` 应能看到 Keycloak 欢迎页。
- 管理控制台在 `https://keycloak.local/admin`。

默认管理员账号（仅开发环境）：

- 用户名：`admin`
- 密码：`admin`

## 6. 常见问题排查

- 访问失败（DNS）：确认 `/etc/hosts` 是否包含上述域名映射。
- 访问失败（Ingress）：确认 `ingress-nginx-controller` 处于 `Running` 状态。
- 证书错误：确认已创建 `ecommerce-tls` Secret，且包含上述主机域名。
- 后端 401/404：本仓库已统一将微服务中的 Keycloak 内部地址修正为 `http://keycloak:8080`，重新部署相关服务。

## 7. 说明

Keycloak 在容器内使用 HTTP（`--http-port=8080`），TLS 在 Ingress 层终止。我们已在部署中加入：

- `--hostname=https://keycloak.local` 与 `--hostname-admin=https://keycloak.local`
- `--hostname-strict=false` 与 `--hostname-strict-https=false`
- `--proxy-headers=xforwarded`

这些设置确保 Keycloak 正确理解通过反向代理传递的 `X-Forwarded-*` 头，并生成以 `https://keycloak.local` 为前端地址的链接。