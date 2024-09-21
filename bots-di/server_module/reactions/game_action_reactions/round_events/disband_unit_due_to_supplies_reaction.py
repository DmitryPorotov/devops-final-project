from DTO.actions.events import ActionDisbandUnitDueToSupplies
from DTO.messages.reply import Reply
from server_module.game_state.game_state import GameState
from server_module.game_state.military_unit import MilitaryUnit
from server_module.reactions.game_action_reactions.base_action_reaction import BaseActionReaction

class DisbandUnitDueToSuppliesReaction(BaseActionReaction):
    def __init__(self, game_state: GameState, reply: Reply[ActionDisbandUnitDueToSupplies]):
        super().__init__(game_state, reply)

    def update_game_state(self):
        pa: ActionDisbandUnitDueToSupplies = self._reply['player_action']
        tile_num = str(pa['tileNumber'])
        army = self._game_state.armies[tile_num]
        unit = MilitaryUnit.from_json(pa['unit'])
        idx = army.index(unit)
        army.pop(idx)
        self.logger.info(pa)
