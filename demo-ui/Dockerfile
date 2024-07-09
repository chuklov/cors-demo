FROM node:20 as build

WORKDIR /demo-ui

COPY package*.json ./

RUN npm install

COPY . .

RUN npm run build

FROM nginx:alpine

COPY --from=build /demo-ui/build /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]