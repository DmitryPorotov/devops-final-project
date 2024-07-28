import json
import os
from dotenv import load_dotenv
from handle_request_for_bots import handle_request_for_bots, send_join_to_chat
from redis_connector import RedisConnector, RedisMessage
from server_module.game_state.game_state import GameState
from server_module.server import Server

load_dotenv()
my_channel = (os.getenv('MY_NAME') + '.*').encode('utf-8')

server = Server()


def handle_requests(message: RedisMessage):
    if message['type'] == 'pmessage' and message['pattern'] == my_channel:
        data = json.loads(message['data'])
        channel = message['channel'].decode('utf-8')
        handle_request_for_bots(data, channel, connection)


def react_to_game(message: RedisMessage):
    if message['type'] == 'pmessage':
        data = json.loads(message['data'])
        channel = message['channel'].decode('utf-8')
        game, worker = channel.split('.')
        game_id = game.split('game')[1]
        if data['action'] == 'join_game':
            server.add_game(game_id, worker)
            for player in data['gameSettings']['players']:
                if player['userId'] < 0:
                    if server.play_as(game_id, player['house']):
                        send_join_to_chat(connection, int(game_id), player['userId'], player['name'])
        elif data['action'] == 'get_game_state':
            server.add_state(game_id, data['gameState'])
            server.add_game_rules(data['gameRules'])
            server.react(game_id, data['gameState']['subPhase'])


connection = RedisConnector(react_to_game)
connection.start(handle_requests)
