import datetime
import json
from typing import TypedDict
from enum import IntEnum
from redis_connector import RedisConnector


class HouseToBotId(IntEnum):
    WOLF = -1
    MOOSE = -2
    PUFFERFISH = -3
    KRAKEN = -4
    ROSE = -5
    LION = -6


class PlayerActionType(TypedDict):
    houseTypes: list[str]


class RequestForBotsMessage(TypedDict):
    userId: int
    lobbyId: int
    type: str
    action: str
    player_action: PlayerActionType


def send_join_to_chat(redis_connector: RedisConnector, game_id: int, user_id: int, bot_name: str):
    time_now = datetime.datetime.now().isoformat()
    message_to_chat = {
        'lobbyId': game_id,
        'userId': user_id,
        'type': 'chat',
        'name': bot_name,
        'time': time_now,
        'body': {
            'type': 'join'
        }
    }
    redis_connector.send_to_chat('chat' + str(game_id), json.dumps(message_to_chat))


def handle_request_for_bots(message: RequestForBotsMessage, channel: str, redis_connector: RedisConnector):
    game_id = message['lobbyId']
    game_channel_prefix = 'game' + str(game_id)
    redis_connector.subscribe(game_channel_prefix)
    worker_name = channel.split('.')[1]
    i = 1
    for house in message['player_action']['houseTypes']:
        bot_name = 'Bot' + str(i)
        i += 1

        message_to_worker = {
            'gameId': str(game_id),
            'userId': HouseToBotId[house.upper()],
            'action': 'join_game',
            'name': bot_name,
            'joinAs': str(HouseToBotId[house.upper()].name.lower())
        }
        redis_connector.send(worker_name + '.' + game_channel_prefix, json.dumps(message_to_worker))
    get_game_state_message = {
        'gameId': str(game_id),
        'userId': -1,
        'action': 'get_game_state'
    }
    redis_connector.send(worker_name + '.' + game_channel_prefix, json.dumps(get_game_state_message))
