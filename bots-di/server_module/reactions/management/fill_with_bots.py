import json
from typing import TypedDict, Optional

from dependency_injector.wiring import inject, Provide

from containers_module import App
from events_service import EventSourcesService
from redis_service import RedisConnector
from server_module.reactions.management.house_to_bot_id import HouseToBotId


class PlayerActionType(TypedDict):
    houseTypes: list[str]


class RequestForBotsMessage(TypedDict):
    userId: int
    lobbyId: int
    type: str
    action: str
    player_action: PlayerActionType


class FillWithBots:
    @inject
    def __init__(self, redis_service=Provide[App.redis_service],
                 events=Provide[App.events]):
        self._redis_service: RedisConnector = redis_service
        self._events: EventSourcesService = events
        self._events.game_management_event_sources.fill_with_bots.subscribe(on_next=self.handle_request_for_bots)

    def handle_request_for_bots(self, msg: tuple[RequestForBotsMessage, str]):
        message, channel = msg
        game_id = message['lobbyId']
        game_channel_prefix = 'game' + str(game_id)
        self._redis_service.subscribe(game_channel_prefix)
        worker_name = channel.split('.')[1]
        i = 1
        for house in message['player_action']['houseTypes']:
            bot_name = 'Bot' + str(i)
            i += 1

            message_to_worker = {
                'gameId': str(game_id),
                'userId': HouseToBotId.get_bot_id_by_house(house),
                'action': 'join_game',
                'name': bot_name,
                'joinAs': house
            }
            self._redis_service.send(worker_name + '.' + game_channel_prefix, json.dumps(message_to_worker))
        get_game_state_message = {
            'gameId': str(game_id),
            'userId': -1,
            'action': 'get_game_state'
        }
        self._redis_service.send(worker_name + '.' + game_channel_prefix, json.dumps(get_game_state_message))
