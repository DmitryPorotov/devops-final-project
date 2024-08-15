from typing import Optional

from dependency_injector.wiring import Provide, inject

from DTO.actions.all_actions import Action
from DTO.messages.messages import MessageGameAction
from DTO.messages.reply import Reply
from containers_module import App
from events_service import EventSourcesService
from redis_service import RedisConnector
from server_module.games_data_service import GamesDataService
from server_module.reactions.game_action_reactions.planning.open_orders_reaction import OpenOrdersReaction
from server_module.reactions.game_phase_reactions.phase_reactor import react_to_phase


class ActionReact:
    game_data: Optional[GamesDataService] = None
    redis: Optional[RedisConnector] = None

    @staticmethod
    @inject
    def init(game_data: GamesDataService = Provide[App.game_manager],
             redis: RedisConnector = Provide[App.redis_service],
             events: EventSourcesService = Provide[App.events]):
        ActionReact.game_data = game_data
        ActionReact.redis = redis
        events.react_to_game_action.subscribe(on_next=ActionReact.react)

    calls = 0
    @staticmethod
    def react(message: MessageGameAction[Action]):
        if 'reply' in message:
            game_id = message['gameId']
            for reply in message['reply']:  # type: Reply[Action]
                action = reply['player_action']
                if action['actionType'] == 'openOrders' and 'orders' in reply['player_action']:
                    OpenOrdersReaction(ActionReact.game_data.get_game(game_id).state, reply).update_game_state()
                # elif

                react_to_phase(game_id, reply['current_phase'])