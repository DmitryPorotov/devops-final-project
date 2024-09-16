from DTO.actions.events import ActionDisableOrder
from DTO.messages.reply import Reply
from server_module.game_state.game_state import GameState
from server_module.game_state.house_type import HouseType
from server_module.game_state.order import Order
from server_module.game_state.order_type import OrderType
from server_module.reactions.game_action_reactions.base_action_reaction import BaseActionReaction

class DisableOrderReaction(BaseActionReaction):
    def __init__(self, game_state: GameState, reply: Reply[ActionDisableOrder]):
        super().__init__(game_state, reply)

    def update_game_state(self):
        pa: ActionDisableOrder = self._reply['player_action']
        for h in HouseType:
            if h is not HouseType.NEUTRAL:
                if (ot := OrderType.from_str(pa['orderType'])) != OrderType.MARCH:
                    self._game_state.available_orders[h][ot] = []
                else:
                    march_orders = self._game_state.available_orders[h][OrderType.MARCH]
                    idx = self._game_state.available_orders[h][OrderType.MARCH].index(Order(OrderType.MARCH, True, 1))
                    march_orders.pop(idx)
        self.logger.info(pa)
