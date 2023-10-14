FROM node:latest as builder

COPY package*.json ./
RUN yarn install
COPY . .
RUN yarn build
FROM nginx:alpine
COPY --from=builder /app/build /usr/share/nginx/html
CMD ["nginx", "-g", "daemon off;"]