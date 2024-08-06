from reactivex import create, Observer, Observable
from reactivex.abc import DisposableBase, SchedulerBase, ObserverBase

from handle_request_for_bots import send_join_to_chat
from redis_connector import RedisConnector
from server_module.server import Server


class CollectionOfSources:

    # join: Observable[]
    def __init__(self,
                 event_source: Observable[tuple[dict, str, str, str]],
                 server: Server,
                 connection: RedisConnector):
        self._event_source = event_source
        self._server = server
        self._connection = connection

    def start(self):
        def func(msg_tuple):
            data, game, worker, game_id = msg_tuple
            replies = None
            if data['action'] == 'join_game':
                self._server.add_game(game_id, worker)
                for player in data['gameSettings']['players']:
                    if player['userId'] < 0:
                        if self._server.play_as(game_id, player['house']):
                            send_join_to_chat(self._connection, int(game_id), player['userId'], player['name'])
            elif data['action'] == 'get_game_state':
                self._server.add_game_rules_and_state(data['gameRules'], game_id, data['gameState'])
                replies = self._server.react(game_id, data['gameState']['subPhase'])

            if replies is not None:
                for r in replies:
                    self._connection.send(worker + '.' + game, json.dumps(r))
        self._event_source.subscribe(on_next=func)
