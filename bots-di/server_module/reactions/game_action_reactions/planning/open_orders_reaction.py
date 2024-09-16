from DTO.actions.planning import ActionOpenOrders
from server_module.game_state.game_state import GameState
from server_module.game_state.house_type import HouseType
from server_module.game_state.order import Order
from server_module.reactions.game_action_reactions.base_action_reaction import BaseActionReaction
from DTO.messages.reply import Reply


class OpenOrdersReaction(BaseActionReaction):
    def __init__(self, game_state: GameState, reply: Reply[ActionOpenOrders]):
        super().__init__(game_state, reply)

    def update_game_state(self):
        pa: ActionOpenOrders = self._reply['player_action']
        if 'orders' in pa:
            self.logger.info(pa)
            orders = pa["orders"]
            for ht, os in orders.items():
                for tn, o in os.items():
                    house = HouseType[ht.upper()]
                    if house not in self._game_state.placed_orders:
                        self._game_state.placed_orders[house] = dict()
                    self._game_state.placed_orders[house][tn] = Order.from_json(o)
