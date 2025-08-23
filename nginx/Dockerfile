# Utilise l'image officielle nginx comme base
FROM nginx:stable

# Conf 
COPY conf.d/default.conf.template /etc/nginx/conf.d/default.conf.template

RUN apt-get update && apt-get install -y gettext-base

CMD envsubst '${SSL_CERT_PATH} ${SSL_KEY_PATH}' < /etc/nginx/conf.d/default.conf.template > /etc/nginx/conf.d/default.conf \
    && nginx -g 'daemon off;'


# Logs redirigés vers stdout/stderr (utile pour Docker logs)
RUN ln -sf /dev/stdout /var/log/nginx/access.log \
 && ln -sf /dev/stderr /var/log/nginx/error.log
