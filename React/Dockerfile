FROM node:18 as builder

WORKDIR /app
COPY package.json yarn.lock ./

RUN yarn install

COPY . .

ENV REACT_APP_ENCRYPTION_KEY=rlaalswodmldbxnqmvmflaldjarPwjddmswkdustksalsekdvoddldlrhqlalfqjsghsmsrhdrogkwldksgrpTek
ENV REACT_APP_DOMAIN=https://3.37.27.227

RUN yarn build

FROM nginx:latest

RUN rm -rf /etc/nginx/conf.d/default.conf

COPY default.conf /etc/nginx/conf.d

COPY --from=builder /app/build /usr/share/nginx/html


EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
