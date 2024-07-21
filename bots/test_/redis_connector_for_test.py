import redis
from redis.client import PubSubWorkerThread
from typing import Optional, Callable, TypedDict


class RedisMessage(TypedDict):
    type: str
    pattern: bytes
    channel: bytes
    data: bytes


class RedisConnectorForTest:
    def __init__(self):
        self._redis = redis.Redis(
            host="localhost", port=6379,
        )
        self._pubsub = self._redis.pubsub()
        self._thread: Optional[PubSubWorkerThread] = None
        self._on_message: Optional[Callable[[RedisMessage], None]] = None

    def start(self, on_message: Callable[[RedisMessage], None]):
        self._pubsub.psubscribe(**{'game2.*': on_message})
        self._on_message = on_message

        def ex_handler(ex):
            print(ex)

        self._thread = self._pubsub.run_in_thread(sleep_time=.001, exception_handler=ex_handler)

    def stop(self):
        if self._thread:
            self._thread.stop()
            self._thread = None

    def send(self, message: str):
        self._redis.publish('worker1.game2', message)
