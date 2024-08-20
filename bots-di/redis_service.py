import redis
from typing import Optional, Callable, TypedDict

from redis.client import PubSubWorkerThread
from base_service import BaseService
from utils_ import print_file_lineno_error


class RedisMessage(TypedDict):
    type: str
    pattern: bytes
    channel: bytes
    data: bytes


class RedisConnector(BaseService):
    def __init__(self, redis_host: str, redis_port: str, my_name: str):
        super().__init__()
        self._redis = redis.Redis(
            host=redis_host, port=redis_port,
        )
        self.my_name = my_name
        self._pubsub = self._redis.pubsub()
        self._thread: Optional[PubSubWorkerThread] = None
        self._on_game_message: Optional[Callable[[RedisMessage], None]] = None
        self._on_game_reset: Optional[Callable[[RedisMessage], None]] = None
        self._on_fill_with_bots_message: Optional[Callable[[RedisMessage], None]] = None

    def start(self):
        self._pubsub.psubscribe(**{self.my_name + '.*': self._on_fill_with_bots_message})
        self._pubsub.psubscribe(**{'new_game.*': self._on_game_reset})

        def ex_handler(ex, arg1, arg2):
            print_file_lineno_error(ex)
            self.logger.critical("Exception in Redis", exc_info=ex, stack_info=True, stacklevel=3)

        self._thread = self._pubsub.run_in_thread(sleep_time=.001, exception_handler=ex_handler)

    def subscribe(self, channel: str):
        self._thread.pubsub.psubscribe(**{channel + '.*': self._on_game_message})

    def unsubscribe(self, channel: str):
        self._thread.pubsub.unsubscribe(channel + '.*')

    def set_new_reset_game_handler(self, handler: Callable[[RedisMessage], None]):
        self._on_game_reset = handler

    def set_react_to_game_handler(self, on_message: Optional[Callable[[RedisMessage], None]]):
        self._on_game_message = on_message

    def set_request_for_bots_handler(self, on_message: Optional[Callable[[RedisMessage], None]]):
        self._on_fill_with_bots_message = on_message

    def stop(self):
        if self._thread:
            self._thread.stop()
            self._thread = None

    def send(self, channel: str, message: str):
        self._redis.publish(channel, message)

    def send_to_chat(self, stream_name: str, message: str):
        self._redis.xadd(stream_name, {'json': message}, '*', maxlen=100)
