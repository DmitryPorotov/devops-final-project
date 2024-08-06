import json
import os
from dotenv import load_dotenv
from handle_request_for_bots import handle_request_for_bots, send_join_to_chat
from observable_sources.collection_of_sources import CollectionOfSources
from redis_connector import RedisConnector, RedisMessage
from reactivex.abc import DisposableBase, SchedulerBase, ObserverBase
from reactivex import create, Observer, Observable
from server_module.server import Server

load_dotenv()
my_channel = (os.getenv('MY_NAME') + '.*').encode('utf-8')

server = Server()
connection = RedisConnector()


def on_subscribe(observer: ObserverBase, scheduler: SchedulerBase) -> DisposableBase:

    def handle_requests(message: RedisMessage):
        if message['type'] == 'pmessage' and message['pattern'] == my_channel:
            data = json.loads(message['data'])
            channel = message['channel'].decode('utf-8')
            handle_request_for_bots(data, channel, connection)

    def new_reset_game_handler(message: RedisMessage):
        if message['type'] == 'message':
            data = json.loads(message['data'])
            server.delete_game(data['gameId'])
            connection.unsubscribe('game' + data['gameId'])

    def react_to_game(message: RedisMessage):
        if message['type'] == 'pmessage':
            data = json.loads(message['data'])
            channel = message['channel'].decode('utf-8')
            game, worker = channel.split('.')
            game_id = game.split('game')[1]
            observer.on_next((data, game, worker, game_id))

    connection.set_react_to_game_handler(react_to_game)
    connection.start(handle_requests)
    connection.set_new_reset_game_handler(new_reset_game_handler)


events_source: Observable[tuple[dict, str, str, str]] = create(on_subscribe)
collection = CollectionOfSources(events_source, server, connection)
collection.start()

