FROM node:18 as builder
WORKDIR /app
COPY package.json .
RUN yarn install
COPY . .
ENV REACT_APP_SPRING_URL=http://172.31.41.246:8080/api
ENV REACT_APP_ENCRYPTION_KEY=rlaalswodmldbxnqmvmflaldjarPwjddmswkdustksalsekdvoddldlrhqlalfqjsghsmsrhdrogkwldksgrpTek
ENV REACT_APP_BASE_URL=http://podo-lotto.site/
RUN yarn build

FROM nginx:latest
RUN rm -rf /etc/nginx/conf.d/default.conf
COPY default.conf /etc/nginx/conf.d

COPY --from=builder /app/build /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]