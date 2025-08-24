#!/bin/sh
set -e

# Génération dynamique du fichier de conf Nginx
envsubst '${SSL_CERT_PATH} ${SSL_KEY_PATH} ${BACKEND_HOST} ${FRONTEND_HOST}' \
  < /etc/nginx/conf.d/default.conf.template \
  > /etc/nginx/conf.d/default.conf

# Lancement de Nginx en mode foreground
exec nginx -g 'daemon off;'
