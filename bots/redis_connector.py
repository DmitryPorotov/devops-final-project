import redis
import os
from redis.client import PubSubWorkerThread
from typing import Optional, Callable, TypedDict
from dotenv import load_dotenv

load_dotenv()

redis_host = os.getenv('REDIS_HOST')
redis_port = os.getenv('REDIS_PORT')
my_name = os.getenv('MY_NAME')


class RedisMessage(TypedDict):
    type: str
    pattern: bytes
    channel: bytes
    data: bytes


class RedisConnector:
    def __init__(self):
        self._redis = redis.Redis(
            host=redis_host, port=redis_port,
        )
        self._pubsub = self._redis.pubsub()
        self._thread: Optional[PubSubWorkerThread] = None
        self._on_message: Optional[Callable[[RedisMessage], None]] = None

    def start(self, on_message: Callable[[RedisMessage], None]):
        self._pubsub.psubscribe(**{my_name + '.*': on_message})

        def ex_handler(ex, arg1, arg2):
            print(ex, arg1, arg2)

        self._thread = self._pubsub.run_in_thread(sleep_time=.001, exception_handler=ex_handler)

    def subscribe(self, channel: str):
        self._thread.pubsub.psubscribe(**{channel + '.*': self._on_message})

    def unsubscribe(self, channel: str):
        self._thread.pubsub.unsubscribe(channel + '.*')

    def set_new_reset_game_handler(self, handler: Callable[[RedisMessage], None]):
        self._thread.pubsub.subscribe(**{'new_game': handler})

    def set_react_to_game_handler(self, on_message: Optional[Callable[[RedisMessage], None]]):
        self._on_message = on_message

    def stop(self):
        if self._thread:
            self._thread.stop()
            self._thread = None

    def send(self, channel: str, message: str):
        self._redis.publish(channel, message)

    def send_to_chat(self, stream_name: str, message: str):
        self._redis.xadd(stream_name, {'json': message}, '*', maxlen=100)
