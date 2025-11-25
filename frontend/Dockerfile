# ===== Stage 1: Build frontend =====
FROM node:22 AS build
WORKDIR /app

# Copy package files
COPY package.json package-lock.json ./

# Cài dependencies
RUN npm install

# Copy toàn bộ source + env
COPY . .
COPY .env .env

# Build production
RUN npm run build

# ===== Stage 2: Export dist =====
FROM alpine:latest
WORKDIR /output

# Copy toàn bộ thư mục dist từ stage build
COPY --from=build /app/dist ./dist

# Khi chạy container, copy dist ra volume ngoài
CMD ["sh", "-c", "mkdir -p /frontend-auth-dist && cp -r /output/dist/* /frontend-auth-dist/"]
