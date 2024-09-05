from DTO.actions.events import ActionWildlingsKillUnit
from DTO.messages.messages import MessageGameAction
from DTO.phases.all_phases import SubPhase
from server_module.game_rules.game_rules import GameRules
from server_module.game_state.game_state import GameState
from server_module.game_state.house_type import HouseType
from server_module.game_state.military_unit import MilitaryUnit
from server_module.game_state.military_unit_type import MilitaryUnitType
from server_module.reactions.game_phase_reactions.base_phase_reaction import BasePhaseReaction
from utils_ import choose_from_list

class WildlingsKillUnitReaction(BasePhaseReaction):
    def __init__(self, game_id: str, house_type: HouseType, game_state: GameState, game_rules: GameRules, phase: SubPhase):
        super().__init__(game_id, house_type, game_state, game_rules, phase)

    def get_actions(self) -> list[MessageGameAction[ActionWildlingsKillUnit]]:
        tiles = self.__get_tile_nums()
        tn = choose_from_list([*tiles.keys()])
        unit = choose_from_list(tiles[tn])
        return [self._to_json(tn, unit)]


    def __get_tile_nums(self):
        armies: dict[str: list[MilitaryUnit]] = {}
        for tn, army in self._game_state.armies.get_armies_by_house_type_generator(self._house_type):  # type: str, list[MilitaryUnit]
            tmp_army = []
            for mu in army:
                if mu.unit_type != MilitaryUnitType.GARRISON and mu.unit_type != MilitaryUnitType.POWER_TOKEN:
                    tmp_army.append(mu)
            if tmp_army:
                armies[tn] = tmp_army
        return armies

    def _to_json(self, tn: int, mu: MilitaryUnit) -> MessageGameAction[ActionWildlingsKillUnit]:
        json = super()._to_json()
        action: ActionWildlingsKillUnit = {
            'houseType': self._house_type,
            'actionType': 'wildlingsKillUnit',
            'tileNumber': tn,
            'unit': mu
        }
        json['player_action'] = action
        return json