from DTO.actions.action import ActionResolveRaidOrder
from DTO.messages.reply import Reply
from server_module.game_state.game_state import GameState
from server_module.game_state.house_type import HouseType
from server_module.reactions.game_action_reactions.base_action_reaction import BaseActionReaction


class ResolveRaidOrderReaction(BaseActionReaction):
    def __init__(self, game_state: GameState, reply: Reply[ActionResolveRaidOrder]):
        super().__init__(game_state, reply)

    def update_game_state(self):
        pa: ActionResolveRaidOrder = self._reply['player_action']
        house = HouseType[pa['houseType'].upper()]
        source_tn = str(pa['sourceTileNumber'])
        self._game_state.available_orders.return_order(house, self._game_state.placed_orders[house][source_tn])
        self._game_state.placed_orders.remove_order(source_tn, pa['houseType'])
        if pa['sourceTileNumber'] != pa['targetTileNumber']:
            target_tn = str(pa['targetTileNumber'])
            target_house = self._game_state.armies[target_tn][0].house
            self._game_state.available_orders.return_order(target_house, self._game_state.placed_orders[target_house][target_tn])
            self._game_state.placed_orders.remove_order(target_tn)
