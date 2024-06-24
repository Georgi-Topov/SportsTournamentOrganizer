FROM node:latest as build

WORKDIR /app

COPY package*.json /app

RUN npm install

COPY . .

RUN ng build --configuration=production

FROM nginx:latest

COPY --from=build app/dist/aftas-angular /usr/share/nginx/html

EXPOSE 80
