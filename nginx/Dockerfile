FROM nginx:stable

# Copie du template
COPY conf.d/default.conf.template /etc/nginx/conf.d/default.conf.template

# Install envsubst pour substitution des variables
RUN apt-get update && apt-get install -y gettext-base

# Redirection des logs vers stdout/stderr pour Docker
RUN ln -sf /dev/stdout /var/log/nginx/access.log \
 && ln -sf /dev/stderr /var/log/nginx/error.log

# Remplacement dynamique des variables et lancement de Nginx
CMD envsubst '${SSL_CERT_PATH} ${SSL_KEY_PATH} ${BACKEND_HOST} ${FRONTEND_HOST}' \
    < /etc/nginx/conf.d/default.conf.template \
    > /etc/nginx/conf.d/default.conf \
    && nginx -g 'daemon off;'
