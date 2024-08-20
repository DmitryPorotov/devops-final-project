import random

from DTO.actions.action import ActionResolveCardLion5
from DTO.messages.messages import MessageGameAction
from DTO.phases.all_phases import SubPhase
from server_module.game_rules.game_rules import GameRules
from server_module.game_state.game_state import GameState
from server_module.game_state.house_type import HouseType
from server_module.reactions.game_phase_reactions.base_phase_reaction import BasePhaseReaction


class ResolveCardLion5Reaction(BasePhaseReaction):
    def __init__(self, game_id: str, house_type: HouseType, game_state: GameState, game_rules: GameRules, phase: SubPhase):
        super().__init__(game_id, house_type, game_state, game_rules, phase)

    def _to_json(self, **kwargs) -> MessageGameAction[ActionResolveCardLion5]:
        json: MessageGameAction[ActionResolveCardLion5] = super()._to_json()
        action: ActionResolveCardLion5 = {
            "houseType": self._house_type,
            "actionType": 'resolveCardLion5',
            "doCancelCard": bool(random.randrange(2))
        }
        json['player_action'] = action
        return json

