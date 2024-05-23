FROM nginx:1.25.4
COPY ./front-defold/build/prod /www/data/html/defold/
COPY ./front-login/build/ /www/data/html/login/
COPY ./nginx.conf.d/prod.conf /etc/nginx/nginx.conf
