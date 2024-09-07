from DTO.actions.events import ActionWildlingsDowngradeKnights
from DTO.messages.messages import MessageGameAction
from DTO.phases.all_phases import SubPhase
from server_module.game_rules.game_rules import GameRules
from server_module.game_state.game_state import GameState
from server_module.game_state.house_type import HouseType
from server_module.game_state.military_unit import MilitaryUnit
from server_module.game_state.military_unit_type import MilitaryUnitType
from server_module.reactions.game_phase_reactions.base_phase_reaction import BasePhaseReaction
from utils_ import choose_from_list


class WildlingsDowngradeKnightsReaction(BasePhaseReaction):
    def __init__(self, game_id: str, house_type: HouseType, game_state: GameState, game_rules: GameRules, phase: SubPhase):
        super().__init__(game_id, house_type, game_state, game_rules, phase)

    def get_actions(self) -> list[MessageGameAction[ActionWildlingsDowngradeKnights]]:
        return [self._to_json(int(choose_from_list(self.__find_tiles_with_knight())))]

    def __find_tiles_with_knight(self) -> list[str]:
        tiles = []
        for tn, army in self._game_state.armies.get_armies_by_house_type_generator(self._house_type):  # type: str, list[MilitaryUnit]
            for mu in army:
                if mu.unit_type is MilitaryUnitType.KNIGHTS:
                    tiles.append(tn)
                    break
        return tiles

    def _to_json(self, tn: int) -> MessageGameAction[ActionWildlingsDowngradeKnights]:
        json = super()._to_json()
        action: ActionWildlingsDowngradeKnights = {
            'houseType': self._house_type,
            'actionType': 'wildlingsDowngradeKnights',
            'tileNumber': tn
        }
        json['player_action'] = action
        return json