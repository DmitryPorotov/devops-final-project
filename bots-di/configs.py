import os

from dotenv import load_dotenv

load_dotenv()

redis_host = os.getenv('REDIS_HOST')
redis_port = os.getenv('REDIS_PORT')
my_name = os.getenv('MY_NAME')
my_channel = (my_name + '.*').encode('utf-8')