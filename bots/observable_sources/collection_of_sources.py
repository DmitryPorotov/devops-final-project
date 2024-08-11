import json

from reactivex import create, Observer, Observable, operators as op
from reactivex.abc import DisposableBase, SchedulerBase, ObserverBase

from handle_request_for_bots import send_join_to_chat
from redis_connector import RedisConnector
from server_module.server import Server
from DTO.actions.planning import *


class PlanningPhaseActionSources:
    addOrder: ActionAddOrder
    removeOrder: ActionRemoveOrder
    openOrders: ActionOpenOrders
    ravenChooseChangeOrderOrLookAtWildlingCard: ActionRavenChooseChangeOrderOrLookAtWildlingCard
    ravenChangeOrder: ActionRavenChangeOrder
    ravenGetWildlingsCard: ActionRavenGetWildlingsCard
    ravenChoosePutWildlingsCardOnTopOrBottom: ActionRavenChoosePutWildlingsCardOnTopOrBottom


class CollectionOfSources:

    # join: Observable[]
    def __init__(self,
                 event_source: Observable[tuple[dict, str, str, str]],
                 server: Server,
                 connection: RedisConnector):
        self._event_source: Observable[tuple[dict, str, str, str]] = event_source
        self._server = server
        self._connection = connection
        self.planning_phase_action_sources = PlanningPhaseActionSources

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
        game_actions_source = self._event_source.pipe(
            op.filter(lambda t: t[0]['action'] == 'game_action')
        )

        self.planning_phase_action_sources.addOrder = game_actions_source.pipe(
            op.filter(lambda t: t[0]['player_action']['actionType'] == 'addOrder')
        )
        self.planning_phase_action_sources.removeOrder = game_actions_source.pipe(
            op.filter(lambda t: t[0]['player_action']['actionType'] == 'removeOrder')
        )
        self.planning_phase_action_sources.openOrders = game_actions_source.pipe(
            op.filter(lambda t: t[0]['player_action']['actionType'] == 'openOrders')
        )
        self.planning_phase_action_sources.ravenChooseChangeOrderOrLookAtWildlingCard = game_actions_source.pipe(
            op.filter(lambda t: t[0]['player_action']['actionType'] == 'ravenChooseChangeOrderOrLookAtWildlingCard')
        )
        self.planning_phase_action_sources.ravenChangeOrder = game_actions_source.pipe(
            op.filter(lambda t: t[0]['player_action']['actionType'] == 'ravenChangeOrder')
        )
        self.planning_phase_action_sources.ravenGetWildlingsCard = game_actions_source.pipe(
            op.filter(lambda t: t[0]['player_action']['actionType'] == 'ravenGetWildlingsCard')
        )
        self.planning_phase_action_sources.ravenChoosePutWildlingsCardOnTopOrBottom = game_actions_source.pipe(
            op.filter(lambda t: t[0]['player_action']['actionType'] == 'ravenChoosePutWildlingsCardOnTopOrBottom')
        )

