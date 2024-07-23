import json
import os
from dotenv import load_dotenv
from handle_request_for_bots import handle_request_for_bots
from redis_connector import RedisConnector, RedisMessage
from server.game_state.game_state import GameState

load_dotenv()
my_channel = (os.getenv('MY_NAME') + '.*').encode('utf-8')


def handle_requests(message: RedisMessage):
    if message['type'] == 'pmessage' and message['pattern'] == my_channel:
        data = json.loads(message['data'])
        channel = message['channel'].decode('utf-8')
        handle_request_for_bots(data, channel, connection)


def react_to_game(message: RedisMessage):

    pass


connection = RedisConnector(react_to_game)
connection.start(handle_requests)
