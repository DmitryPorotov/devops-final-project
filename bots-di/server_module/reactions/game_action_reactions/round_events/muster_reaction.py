from DTO.actions.events import ActionMuster
from DTO.messages.reply import Reply
from server_module.game_state.game_state import GameState
from server_module.game_state.house_type import HouseType
from server_module.game_state.military_unit import MilitaryUnit
from server_module.game_state.military_unit_type import MilitaryUnitType
from server_module.reactions.game_action_reactions.action.common import subtract_army
from server_module.reactions.game_action_reactions.base_action_reaction import BaseActionReaction


class MusterReaction(BaseActionReaction):
    def __init__(self, game_state: GameState, reply: Reply[ActionMuster]):
        super().__init__(game_state, reply)

    def update_game_state(self):
        pa: ActionMuster = self._reply['player_action']
        from_tile = str(pa['fromTile'])
        to_tile = str(pa['toTile']) if 'toTile' in pa else from_tile
        house = HouseType[pa['houseType'].upper()]
        if 'isUpgrade' in pa and pa['isUpgrade']:
            subtract_army(self._game_state.armies[from_tile], [MilitaryUnit(house, MilitaryUnitType.FOOTMEN)])
        if to_tile not in self._game_state.armies:
            self._game_state.armies[to_tile] = []
        self._game_state.armies[to_tile].append(MilitaryUnit.from_json(pa['unitToMuster']))
        for tile, points in pa['usedPoints'].items():
            self._game_state.used_mustering_points[str(tile)] = points
