#!/usr/bin/env bash
set -euo pipefail

# One-click local bootstrap for Docker Desktop + Kubernetes
# - Installs ingress-nginx
# - Creates TLS secret from repo certificates
# - Adds /etc/hosts mappings
# - Applies infra services and microservices
# - Applies ingress and probes Keycloak

ROOT_DIR="$(cd "$(dirname "$0")"/.. && pwd)"

CERT_PEM="$ROOT_DIR/keycloak.local+2.pem"
KEY_PEM="$ROOT_DIR/keycloak.local+2-key.pem"

ensure_certs() {
  if [[ -f "$CERT_PEM" && -f "$KEY_PEM" ]]; then
    echo "TLS cert files found:"
    echo "  - $CERT_PEM"
    echo "  - $KEY_PEM"
    return 0
  fi

  echo "TLS cert files not found. Attempting to generate..."

  if command -v mkcert >/dev/null 2>&1; then
    echo "mkcert detected. Generating local trusted certificates..."
    mkcert -install >/dev/null 2>&1 || true
    mkcert keycloak.local api.local redis.local >/dev/null 2>&1 || mkcert keycloak.local api.local redis.local
  elif command -v brew >/dev/null 2>&1; then
    echo "mkcert not found. Installing mkcert via Homebrew..."
    brew install mkcert nss >/dev/null 2>&1 || brew install mkcert nss
    mkcert -install >/dev/null 2>&1 || true
    mkcert keycloak.local api.local redis.local >/dev/null 2>&1 || mkcert keycloak.local api.local redis.local
  else
    echo "Homebrew not available. Falling back to OpenSSL self-signed certificate (browser may warn)."
    OPENSSL_CNF="$ROOT_DIR/tmp-openssl.cnf"
    cat > "$OPENSSL_CNF" <<EOF
[req]
default_bits = 2048
prompt = no
default_md = sha256
req_extensions = req_ext
distinguished_name = dn

[dn]
CN = keycloak.local

[req_ext]
subjectAltName = @alt_names

[alt_names]
DNS.1 = keycloak.local
DNS.2 = api.local
DNS.3 = redis.local
EOF
    openssl req -x509 -nodes -days 365 -newkey rsa:2048 \
      -keyout "$KEY_PEM" -out "$CERT_PEM" -config "$OPENSSL_CNF"
    rm -f "$OPENSSL_CNF"
    echo "Generated self-signed cert:"
    echo "  - $CERT_PEM"
    echo "  - $KEY_PEM"
    echo "Note: Your browser may show a certificate warning."
  fi

  if [[ ! -f "$CERT_PEM" || ! -f "$KEY_PEM" ]]; then
    echo "ERROR: Failed to generate TLS certificates. Please generate manually: mkcert keycloak.local api.local redis.local"
    exit 1
  fi
}

echo "[1/7] Installing ingress-nginx controller..."
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/main/deploy/static/provider/cloud/deploy.yaml >/dev/null
kubectl -n ingress-nginx rollout status deploy/ingress-nginx-controller --timeout=120s

echo "[2/7] Creating/Updating TLS secret 'ecommerce-tls'..."
ensure_certs
kubectl create secret tls ecommerce-tls \
  --namespace default \
  --cert="$CERT_PEM" \
  --key="$KEY_PEM" \
  --dry-run=client -o yaml | kubectl apply -f -

echo "[3/7] Creating/Updating Keycloak realm ConfigMap (using full backups)..."
kubectl create configmap keycloak-realm \
  --namespace default \
  --from-file="$ROOT_DIR/k8s/services/keycloak/configuration/ecomm-app-realm.json" \
  --from-file="$ROOT_DIR/k8s/services/keycloak/configuration/ecomm-app-users-0.json" \
  --dry-run=client -o yaml | kubectl apply -f -

echo "[4/7] Adding hosts entries (requires sudo, will skip if not available)..."
LINE="127.0.0.1 keycloak.local api.local redis.local"
if ! grep -q "keycloak.local" /etc/hosts; then
  if sudo -n true 2>/dev/null; then
    echo "$LINE" | sudo tee -a /etc/hosts >/dev/null
  else
    echo "No passwordless sudo; skipping hosts update. Please add manually if needed: $LINE"
  fi
else
  echo "Hosts already contain keycloak.local entries. Skipping."
fi

echo "[5/7] Applying infra services (Redis/MySQL/Kafka/Keycloak/etc.)..."
# Apply Keycloak via Kustomize when present
if [[ -f "$ROOT_DIR/k8s/services/keycloak/kustomization.yml" ]]; then
  kubectl apply -k "$ROOT_DIR/k8s/services/keycloak" >/dev/null
fi
# Apply other service manifests, excluding kustomization.yml files
find "$ROOT_DIR/k8s/services" -type f \( -name '*.yml' -o -name '*.yaml' \) -not -name 'kustomization.yml' -print0 | xargs -0 -n1 kubectl apply -f >/dev/null

echo "[6/9] Creating/Updating microservice Secrets from .env files (if present)..."
create_service_secrets() {
  local services=(address cart product order payment inventory gateway user)
  for svc in "${services[@]}"; do
    local env_file="$ROOT_DIR/$svc/.env"
    if [[ -f "$env_file" ]]; then
      echo "  - $svc: loading $env_file into secret '$svc-secret'"
      kubectl create secret generic "$svc-secret" \
        --namespace default \
        --from-env-file="$env_file" \
        --dry-run=client -o yaml | kubectl apply -f - >/dev/null
    else
      echo "  - $svc: .env not found, skipping secret creation"
    fi
  done
}
create_service_secrets

echo "Creating/Updating gateway truststore secret..."
GATEWAY_TRUSTSTORE="$ROOT_DIR/tmp/keycloak-local-truststore.jks"
if [[ ! -f "$GATEWAY_TRUSTSTORE" ]]; then
  echo "Generating JKS truststore from $CERT_PEM ..."
  keytool -importcert -file "$CERT_PEM" -alias keycloak-local -keystore "$GATEWAY_TRUSTSTORE" -storepass changeit -noprompt
fi
kubectl create secret generic gateway-truststore \
  --namespace default \
  --from-file=keycloak-local-truststore.jks="$GATEWAY_TRUSTSTORE" \
  --dry-run=client -o yaml | kubectl apply -f - >/dev/null

echo "Waiting for Keycloak to become Ready (pre-gateway rollout)..."
kubectl rollout status deploy/keycloak --timeout=180s || true

echo "[7/9] Applying application microservices (ConfigMaps/Deployments/Services)..."
find "$ROOT_DIR/k8s/bootstrap" -type f \( -name '*.yml' -o -name '*.yaml' \) -print0 | xargs -0 -n1 kubectl apply -f >/dev/null

echo "[8/9] Applying ingress..."
kubectl apply -f "$ROOT_DIR/k8s/ingress/ingress.yml" >/dev/null

echo "Resolving current Ingress controller ClusterIP for hostAliases..."
INGRESS_IP=$(kubectl get svc -n ingress-nginx ingress-nginx-controller -o jsonpath='{.spec.clusterIP}')
if [[ -z "${INGRESS_IP:-}" ]]; then
  echo "ERROR: Failed to resolve ingress-nginx controller ClusterIP. Aborting gateway apply."
  exit 1
fi
echo "Ingress IP detected: $INGRESS_IP"

echo "Re-applying Deployments with updated hostAliases IP ($INGRESS_IP)..."
services=(gateway user product cart order payment inventory address)
for svc in "${services[@]}"; do
  deploy_file="$ROOT_DIR/k8s/bootstrap/$svc/deployment.yml"
  if [[ -f "$deploy_file" ]]; then
    TMP_DEPLOY=$(mktemp)
    cp "$deploy_file" "$TMP_DEPLOY"
    sed -E "s/(^[[:space:]]*- ip: \"?)[0-9\.]+(\"?)/\1$INGRESS_IP\2/" "$TMP_DEPLOY" | kubectl apply -f - >/dev/null
    rm -f "$TMP_DEPLOY"
    echo "  - $svc applied"
  fi
done

echo "Waiting for gateway rollout status..."
kubectl rollout status deploy/gateway --timeout=180s || true

echo "Probing https://keycloak.local ..."
set +e
curl -I https://keycloak.local 2>/dev/null | sed -n '1,10p'
set -e

echo "Probing in-cluster https://keycloak.local ..."
set +e
kubectl run curl --namespace default --image=curlimages/curl:8.11.0 -i --rm --restart=Never -- curl -sS -kI https://keycloak.local 2>/dev/null | sed -n '1,10p'
set -e

echo
echo "Done. Visit:"
echo "  - https://keycloak.local/ (will redirect to admin UI)"
echo "If you see temporary 503, wait ~30s for pods to warm up and retry."