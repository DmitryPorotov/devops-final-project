import json

from reactivex import Observable, Subject
from reactivex.abc import ObserverBase, SchedulerBase, DisposableBase

from DTO.messages.messages import Message
from base_service import BaseService
from configs import my_channel
from redis_service import RedisConnector, RedisMessage
from reactivex import operators as op


class GameManagementEventSources:
    fill_with_bots: Subject
    react_to_game: Subject[tuple[dict, str, str, str]]
    reset_game: Subject


class ReactToGameEventSources:
    message_join_game: Observable[Message]
    message_get_game_state: Observable[Message]
    message_start_game: Observable[Message]  # still not used, game starts when it's created, even before everyone joins


class EventSourcesService(BaseService):
    def __init__(self, redis_service: RedisConnector):
        super().__init__()
        self._redis_service = redis_service
        self.game_management_event_sources = GameManagementEventSources
        self.react_to_game_event_sources = ReactToGameEventSources

        self.game_management_event_sources.react_to_game = Subject()

        def react_to_game(message: RedisMessage):
            if message['type'] == 'pmessage':
                data = json.loads(message['data'])
                channel = message['channel'].decode('utf-8')
                game, worker = channel.split('.')
                game_id = game.split('game')[1]
                self.game_management_event_sources.react_to_game.on_next((data, game, worker, game_id))

        redis_service.set_react_to_game_handler(react_to_game)

        self.game_management_event_sources.fill_with_bots = Subject()

        def handle_requests_for_bots(message: RedisMessage):
            if message['type'] == 'pmessage' and message['pattern'] == my_channel:
                data = json.loads(message['data'])
                channel = message['channel'].decode('utf-8')
                self.game_management_event_sources.fill_with_bots.on_next((data, channel))

        redis_service.set_request_for_bots_handler(handle_requests_for_bots)

        self.game_management_event_sources.reset_game = Subject()

        def new_reset_game_handler(message: RedisMessage):
            if message['type'] == 'message':
                data = json.loads(message['data'])
                redis_service.unsubscribe('game' + data['gameId'])
                self.game_management_event_sources.reset_game.on_next((data, message['channel'].decode('utf-8')))

        redis_service.set_new_reset_game_handler(new_reset_game_handler)

        self.react_to_game_event_sources.message_join_game = self.game_management_event_sources.react_to_game.pipe(
            op.filter(lambda t: t[0]['action'] == 'join_game' and t[0]['type'] == 'action'),
        )

        react_to_game_message_only = self.game_management_event_sources.react_to_game.pipe(
            op.map(lambda t: t[0]),
            op.filter(lambda t: t['type'] == 'action')
        )

        self.react_to_game_event_sources.message_get_game_state = react_to_game_message_only.pipe(
            op.filter(lambda t: t['action'] == 'get_game_state'),
        )
