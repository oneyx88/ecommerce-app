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

echo "[3/7] Adding hosts entries (requires sudo)..."
LINE="127.0.0.1 keycloak.local api.local redis.local"
if ! grep -q "keycloak.local" /etc/hosts; then
  echo "$LINE" | sudo tee -a /etc/hosts >/dev/null
else
  echo "Hosts already contain keycloak.local entries. Skipping."
fi

echo "[4/7] Applying infra services (Redis/MySQL/Kafka/Keycloak/etc.)..."
kubectl apply -R -f "$ROOT_DIR/k8s/services/" >/dev/null

echo "[5/7] Applying application microservices (ConfigMaps/Secrets/Deployments/Services)..."
kubectl apply -R -f "$ROOT_DIR/k8s/bootstrap/" >/dev/null

echo "[6/7] Applying ingress..."
kubectl apply -f "$ROOT_DIR/k8s/ingress/ingress.yml" >/dev/null

echo "[7/7] Waiting for Keycloak to become Ready..."
kubectl rollout status deploy/keycloak --timeout=180s || true

echo "Probing https://keycloak.local ..."
set +e
curl -I https://keycloak.local 2>/dev/null | sed -n '1,10p'
set -e

echo
echo "Done. Visit:"
echo "  - https://keycloak.local/ (will redirect to admin UI)"
echo "If you see temporary 503, wait ~30s for pods to warm up and retry."