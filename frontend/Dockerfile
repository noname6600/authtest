# ===== Stage 1: Build FE =====
FROM node:22 AS build
WORKDIR /app

# Copy package.json + lock
COPY package.json package-lock.json ./
RUN npm install

# Copy toàn bộ source
COPY . .

# Build production
RUN npm run build

# ===== Stage 2: Serve bằng nginx =====
FROM nginx:alpine
# Copy dist từ stage build vào thư mục nginx serve
COPY --from=build /app/dist /usr/share/nginx/html

# Expose port 80
EXPOSE 80

# Run nginx
CMD ["nginx", "-g", "daemon off;"]
