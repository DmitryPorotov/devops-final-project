#!/bin/bash -e

wget https://github.com/defold/defold/releases/download/1.9.1/bob.jar &&\
docker build --tag bob-the-builder . &&\
docker tag bob-the-builder:latest localhost:30500/bob-the-builder:latest &&\
docker push localhost:30500/bob-the-builder:latest
