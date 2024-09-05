from DTO.actions.events import ActionWildlingsUpgradeKnights
from DTO.messages.messages import MessageGameAction
from DTO.phases.all_phases import SubPhase
from server_module.game_rules.game_rules import GameRules
from server_module.game_state.game_state import GameState
from server_module.game_state.house_type import HouseType
from server_module.reactions.game_phase_reactions.base_phase_reaction import BasePhaseReaction

class WildlingsUpgradeKnightsReaction(BasePhaseReaction):
    def __init__(self, game_id: str, house_type: HouseType, game_state: GameState, game_rules: GameRules, phase: SubPhase):
        super().__init__(game_id, house_type, game_state, game_rules, phase)

    def get_actions(self) -> list[MessageGameAction[ActionWildlingsUpgradeKnights]]:
        raise Exception('Unimplemented!')

    def _to_json(self, tn1: int, tn2: int = None) -> MessageGameAction[ActionWildlingsUpgradeKnights]:
        json = super()._to_json()
        action: ActionWildlingsUpgradeKnights = {
            'houseType': self._house_type,
            'actionType': 'wildlingsDowngradeKnights',
            'tileNumber1': tn1,
            'tileNumber2': tn2,
        }
        json['player_action'] = action
        return json