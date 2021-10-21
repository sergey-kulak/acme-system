FROM node:14.18.1-alpine3.14 as build

RUN npm install -g create-react-app
WORKDIR /build
COPY package.json .
RUN npm install
COPY public/  public/
COPY src/ src/
RUN npm run build

FROM nginx:1.21.3-alpine

COPY --from=build /build/build /usr/share/nginx/html
COPY config/nginx-custom.conf /etc/nginx/conf.d/default.conf