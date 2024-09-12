from dependency_injector.wiring import inject, Provide

from DTO.actions.action import ActionCleanUpAfterRound
from DTO.messages.reply import Reply
from containers_module import App
from server_module.game_state.game_state import GameState
from server_module.game_state.placed_orders import PlacedOrders
from server_module.games_data_service import GamesDataService
from server_module.reactions.game_action_reactions.base_action_reaction import BaseActionReaction


class CleanUpAfterRoundReaction(BaseActionReaction):
    def __init__(self, game_state: GameState, reply: Reply[ActionCleanUpAfterRound]):
        super().__init__(game_state, reply)

    @inject
    def update_game_state(self, games_service: GamesDataService = Provide[App.game_service]):
        pa: ActionCleanUpAfterRound = self._reply['player_action']
        self._game_state['placedOrders'] = self._game_state.placed_orders = PlacedOrders()
        self._game_state['availableOrders'] = self._game_state.available_orders = (
            self._game_state.available_orders.build_from_placed_orders(games_service.game_rules, self._game_state.placed_orders))
        self._game_state.dominance_tokens_usage['valyrianSword'] = False
        self._game_state.dominance_tokens_usage['messengerRaven'] = False
        self._game_state.used_mustering_points.clear()
        self._game_state.round_counter = pa['round']
        for tn, army in self._game_state.armies.items():
            for mu in army:
                mu.is_defeated = mu['isDefeated'] = False