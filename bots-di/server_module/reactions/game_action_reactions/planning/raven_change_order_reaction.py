from DTO.actions.planning import ActionRavenChangeOrder
from DTO.messages.reply import Reply
from server_module.game_state.game_state import GameState
from server_module.game_state.house_type import HouseType
from server_module.game_state.order import Order
from server_module.reactions.game_action_reactions.base_action_reaction import BaseActionReaction


class RavenChangeOrderReaction(BaseActionReaction):
    def __init__(self, game_state: GameState, reply: Reply[ActionRavenChangeOrder]):
        super().__init__(game_state, reply)

    def update_game_state(self):
        pa: ActionRavenChangeOrder = self._reply['player_action']
        order = Order.from_json(pa['order'])
        house = HouseType[pa['houseType'].upper()]
        tile_num = str(pa['tileNumber'])
        self._game_state.available_orders.return_order(house, self._game_state.placed_orders[house][tile_num])
        self._game_state.available_orders.use_order(house, order)

        self._game_state.placed_orders[house][tile_num] = order
        self._game_state.dominance_tokens_usage['messengerRaven'] = True
        self.logger.info(pa)
