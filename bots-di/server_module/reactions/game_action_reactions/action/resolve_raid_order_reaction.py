from DTO.actions.action import ActionResolveRaidOrder
from DTO.messages.reply import Reply
from server_module.game_state.game_state import GameState
from server_module.reactions.game_action_reactions.base_action_reaction import BaseActionReaction


class ResolveRaidOrderReaction(BaseActionReaction):
    def __init__(self, game_state: GameState, reply: Reply[ActionResolveRaidOrder]):
        super().__init__(game_state, reply)

    def update_game_state(self):
        action: ActionResolveRaidOrder = self._reply['player_action']
        self._game_state.placed_orders.remove_order(str(action['sourceTileNumber']), action['houseType'])
        if action['sourceTileNumber'] != action['targetTileNumber']:
            self._game_state.placed_orders.remove_order(str(action['targetTileNumber']))
