docker tag web_server_prod:latest localhost:30500/web_server_prod:latest &&\
docker push localhost:30500/web_server_prod:latest
docker tag worker_prod:latest localhost:30500/worker_prod:latest &&\
docker push localhost:30500/worker_prod:latest
docker tag nginx_prod:latest localhost:30500/nginx_prod:latest &&\
docker push localhost:30500/nginx_prod:latest
