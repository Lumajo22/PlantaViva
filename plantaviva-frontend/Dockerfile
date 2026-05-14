# ════════════════════════════════════════════════════════════════════
#   PlantaViva Frontend — Dockerfile
#   Imagen: nginx:alpine
#   Puerto: 80
# ════════════════════════════════════════════════════════════════════

# Etapa 1: Build con Node
FROM node:20-alpine AS build

WORKDIR /app

# Copiar package.json y package-lock.json
COPY package*.json ./

# Instalar dependencias
RUN npm ci

# Copiar código fuente
COPY . .

# Build de producción
RUN npm run build

# Etapa 2: Servir con Nginx
FROM nginx:alpine

# Copiar build al directorio de Nginx
COPY --from=build /app/dist /usr/share/nginx/html

# Copiar configuración personalizada de Nginx
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Exponer puerto
EXPOSE 80

# Comando de inicio (Nginx por defecto)
CMD ["nginx", "-g", "daemon off;"]