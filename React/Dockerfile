FROM node:alpine as builder
WORKDIR /React
COPY package.json .
RUN yarn install
COPY . .
RUN yarn build

FROM nginx:latest
RUN rm /etc/nginx/conf.d/default.conf
COPY ./nginx.conf /etc/nginx/conf.d

COPY --from=builder /React/build /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]