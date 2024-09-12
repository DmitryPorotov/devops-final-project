from utils_ import randrange

from DTO.actions.action import ActionUseValyrianSteelBlade
from DTO.messages.messages import MessageGameAction
from DTO.phases.all_phases import SubPhase
from server_module.game_rules.game_rules import GameRules
from server_module.game_state.game_state import GameState
from server_module.game_state.house_type import HouseType
from server_module.reactions.game_phase_reactions.base_phase_reaction import BasePhaseReaction


class ChooseToUseValyrianSteelBladeReaction(BasePhaseReaction):
    def __init__(self, game_id: str, house_type: HouseType, game_state: GameState, game_rules: GameRules, phase: SubPhase):
        super().__init__(game_id, house_type, game_state, game_rules, phase)

    def get_actions(self) -> list[MessageGameAction[ActionUseValyrianSteelBlade]]:
        choices = ['nothing', 'nothing', 'plusOne', 'changeTOBCard']
        idx = randrange(len(choices))
        if self._game_state.dominance_tokens_usage['valyrianSword']:
            idx = 0
        return [self._to_json(choices[idx])]

    def _to_json(self, choice: str) -> MessageGameAction[ActionUseValyrianSteelBlade]:
        json = super()._to_json()
        action: ActionUseValyrianSteelBlade = {
            'houseType': self._house_type,
            'actionType': 'useValyrianSteelBlade',
            'choice': choice
        }
        json['player_action'] = action
        self.logger.info(json)
        return json
