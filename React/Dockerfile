FROM node:alpine as builder
COPY ./package* /usr/src/app/
WORKDIR /usr/src/app
RUN yarn install
COPY . /usr/src/app
RUN yarn build

FROM nginx:latest
RUN rm -rf /etc/nginx/conf.d 
COPY conf /etc/nginx

COPY --from=builder /usr/src/app/build /usr/share/nginx/html
CMD ["nginx", "-g", "daemon off;"]