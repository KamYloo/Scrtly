#!/bin/sh
cat > /app/dist/env.js <<EOF
window.__ENV = {
  VITE_APP_BACKEND_URL: "${VITE_APP_BACKEND_URL:-}",
  VITE_STRIPE_PUBLIC_KEY: "${VITE_STRIPE_PUBLIC_KEY:-}"
};
EOF

exec "$@"

