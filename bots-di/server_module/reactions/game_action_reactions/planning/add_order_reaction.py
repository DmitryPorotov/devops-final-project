from DTO.actions.planning import ActionOpenOrders, ActionAddOrder
from server_module.game_state.game_state import GameState
from server_module.game_state.house_type import HouseType
from server_module.game_state.order import Order
from server_module.game_state.order_type import OrderType
from server_module.game_state.track_type import TrackType
from server_module.reactions.game_action_reactions.base_action_reaction import BaseActionReaction
from DTO.messages.reply import Reply


#note this class is useless because order is not returned from worker, it's closed
class AddOrderReaction(BaseActionReaction):
    def __init__(self, game_state: GameState, reply: Reply[ActionOpenOrders]):
        super().__init__(game_state, reply)

    def update_game_state(self):
        pa: ActionAddOrder = self._reply['player_action']
        if pa['order'] is not None:
            order = Order(
                OrderType[pa['order']['type'].upper()],
                pa['order']['isStar'] if 'isStar' in pa['order'] else False,
                pa['order']['modifier'] if 'modifier' in pa['order'] else 0,
            )
            house = HouseType[pa['houseType'].upper()]
            self._game_state.available_orders.use_order(house, order)
            pos_at_court = self._game_state.tracks[TrackType.COURT].index(HouseType[pa["houseType"].upper()])
            self._game_state.placed_orders.place_order(house, pa['tileNumber'], order, pos_at_court)

        self.logger.info(pa)
