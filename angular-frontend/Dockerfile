# Étape 1 : Build de l'app Angular
FROM node:20-alpine AS builder

WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .

# Ajoute la variable d'environnement ici pour la build
ENV NODE_OPTIONS=--openssl-legacy-provider

RUN npm run build --prod

# Étape 2 : Serveur Nginx
FROM nginx:alpine

# Supprime la config par défaut (optionnel si tu veux la tienne)
RUN rm -rf /usr/share/nginx/html/*

# Copie les fichiers buildés d’Angular dans le dossier servi par nginx
COPY --from=builder /app/dist/angular-frontend /usr/share/nginx/html

# Copie ta propre config nginx si besoin (facultatif)
# COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

HEALTHCHECK --interval=30s --timeout=3s --start-period=10s --retries=3 \
  CMD curl -fsS http://localhost/ || exit 1

CMD ["nginx", "-g", "daemon off;"]
