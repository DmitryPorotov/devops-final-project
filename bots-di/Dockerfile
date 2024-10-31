FROM python:3.11-alpine

WORKDIR /usr/src/bots

COPY requirements.txt ./

RUN pip install --no-cache-dir -r requirements.txt

COPY . .

CMD [ "python", "./__main__.py" ]