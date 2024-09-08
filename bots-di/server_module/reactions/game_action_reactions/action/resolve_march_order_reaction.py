from DTO.actions.action import ActionResolveMarchOrder
from DTO.messages.reply import Reply
from server_module.game_state.combat import Combat
from server_module.game_state.game_state import GameState
from server_module.game_state.house_type import HouseType
from server_module.game_state.military_unit import MilitaryUnit
from server_module.game_state.military_unit_type import MilitaryUnitType
from server_module.reactions.game_action_reactions.action.common import subtract_army
from server_module.reactions.game_action_reactions.base_action_reaction import BaseActionReaction


class ResolveMarchOrderReaction(BaseActionReaction):
    def __init__(self, game_state: GameState, reply: Reply[ActionResolveMarchOrder]):
        super().__init__(game_state, reply)

    def update_game_state(self):
        pa: ActionResolveMarchOrder = self._reply['player_action']
        is_combat = 'combat' in self._reply
        house = HouseType[pa['houseType'].upper()]
        source_tn = str(pa['sourceTileNumber'])

        if pa['targets'] is None:
            return

        for tn, mil_units_json in pa['targets'].items():
            mil_units: list[MilitaryUnit] = [*(MilitaryUnit.from_json(j) for j in mil_units_json)]
            subtract_army(self._game_state.armies[source_tn], mil_units)
            if not self._game_state.armies[source_tn]:
                del self._game_state.armies[source_tn]
            if not is_combat:
                if tn in self._game_state.armies:
                    for i, mu in enumerate(self._game_state.armies[tn]):
                        if mu.unit_type is MilitaryUnitType.POWER_TOKEN and mu.house != house:
                            self._game_state.armies[tn].pop(i)
                    self._game_state.armies[tn].extend(mil_units)
                else:
                    self._game_state.armies[tn] = mil_units
        if is_combat:
            self._game_state.combat = Combat.from_json(self._reply['combat'])
