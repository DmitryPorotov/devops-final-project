from DTO.actions.events import ActionWildlingsKillUnit
from DTO.messages.messages import MessageGameAction
from DTO.phases.all_phases import SubPhase
from server_module.game_rules.game_rules import GameRules
from server_module.game_state.game_state import GameState
from server_module.game_state.house_type import HouseType
from server_module.game_state.military_unit import MilitaryUnit
from server_module.reactions.game_phase_reactions.base_phase_reaction import BasePhaseReaction
from utils_ import choose_from_list

class WildlingsKillUnitReaction(BasePhaseReaction):
    def __init__(self, game_id: str, house_type: HouseType, game_state: GameState, game_rules: GameRules, phase: SubPhase):
        super().__init__(game_id, house_type, game_state, game_rules, phase)

    def get_actions(self) -> list[MessageGameAction[ActionWildlingsKillUnit]]:
        raise Exception('Unimplemented!')

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