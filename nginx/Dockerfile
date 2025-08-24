# Base officielle Nginx
FROM nginx:stable

# Copie du template de configuration
COPY conf.d/default.conf.template /etc/nginx/conf.d/default.conf.template

# Copie de l'entrypoint pour générer la config dynamique
COPY docker-entrypoint.sh /docker-entrypoint.sh
RUN chmod +x /docker-entrypoint.sh

# Install envsubst pour la substitution des variables
RUN apt-get update && apt-get install -y gettext-base \
    && ln -sf /dev/stdout /var/log/nginx/access.log \
    && ln -sf /dev/stderr /var/log/nginx/error.log

# Par défaut, le service utilisera notre entrypoint
ENTRYPOINT ["/docker-entrypoint.sh"]