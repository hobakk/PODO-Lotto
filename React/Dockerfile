FROM node:alpine as builder

WORKDIR /app

COPY package*.json ./

RUN yarn install

COPY . .

RUN yarn build

FROM nginx

EXPOSE 3000

COPY nginx.conf /etc/nginx/nginx.conf

COPY --from=builder /app/build /usr/share/nginx/html

CMD ["nginx", "-g", "daemon off;"]