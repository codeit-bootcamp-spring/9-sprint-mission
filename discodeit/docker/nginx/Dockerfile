FROM nginx:1.27-alpine

COPY docker/nginx/default.conf /etc/nginx/conf.d/default.conf
COPY src/main/resources/static/ /usr/share/nginx/html/

EXPOSE 80
