import datetime
import json

from dependency_injector.wiring import inject, Provide

from containers_module import App
from events_service import EventSourcesService
from redis_service import RedisConnector
from server_module.games_data_service import GamesDataService


class JoinGame:
    @inject
    def __init__(self,
                 events_service: EventSourcesService = Provide[App.events],
                 games_data: GamesDataService = Provide[App.game_manager],
                 redis: RedisConnector = Provide[App.redis_service]):
        events_service.react_to_game_event_sources.message_join_game.subscribe(on_next=self.on_join_game)
        self._games_data = games_data
        self._redis = redis

    def on_join_game(self, msg):
        data, game, worker, game_id = msg
        self._games_data.add_game(game_id, worker)
        for player in data['gameSettings']['players']:
            if player['userId'] < 0:
                if self._games_data.play_as(game_id, player['house']):
                    send_join_to_chat(self._redis, int(game_id), player['userId'], player['name'])


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