import json
import uuid

from dependency_injector.wiring import Provide, inject

from DTO.messages.messages import Message
from containers_module import App
from events_service import EventSourcesService
from redis_service import RedisConnector
from server_module.games_data_service import GamesDataService


class RoundEventsMainPhaseEnd:
    @inject
    def __init__(self, events_service: EventSourcesService = Provide[App.events], redis: RedisConnector = Provide[App.redis_service], games_data: GamesDataService = Provide[App.game_manager]):
        events_service.react_to_game_event_sources.message_switch_to_planning_phase.subscribe(on_next=self.on_switch_to_action_phase)
        self._redis = redis
        self._game_data = games_data

    def on_switch_to_action_phase(self, msg: Message):
        game = self._game_data.get_game(msg['gameId'])
        m: Message = {
            "type": 'action',
            "userId": -1,
            "gameId": msg['gameId'],
            "action": 'get_partial_game_state',
            'messageId': str(uuid.uuid4()),
            'parts': ['tracks', 'armies', 'discardedHouseCards', 'powerTokens', 'availableOrders', 'wildlingCounter']
        }
        self._redis.send(game.worker + ".game" + msg['gameId'], json.dumps(m))