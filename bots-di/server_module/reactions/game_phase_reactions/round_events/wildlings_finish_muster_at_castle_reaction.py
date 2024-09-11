from random import randrange

from DTO.actions.events import ActionWildlingsMusterAtCastle
from DTO.messages.messages import MessageGameAction
from DTO.phases.all_phases import SubPhase
from server_module.game_rules.game_rules import GameRules
from server_module.game_state.game_state import GameState
from server_module.game_state.house_type import HouseType
from server_module.game_state.military_unit import MilitaryUnit
from server_module.reactions.game_phase_reactions.base_phase_reaction import BasePhaseReaction


class WildlingsFinishMusterAtCastleReaction(BasePhaseReaction):
    def __init__(self, game_id: str, house_type: HouseType, game_state: GameState, game_rules: GameRules, phase: SubPhase):
        super().__init__(game_id, house_type, game_state, game_rules, phase)

    def get_actions(self) -> list[MessageGameAction[ActionWildlingsMusterAtCastle]]:
        return [self._to_json(1, [])]

    def _to_json(self, tn: int, mus: list[tuple[int,bool,MilitaryUnit]]) -> MessageGameAction[ActionWildlingsMusterAtCastle]:
        json = BasePhaseReaction._to_json(self)
        action: ActionWildlingsMusterAtCastle = {
            'houseType': self._house_type,
            'actionType': 'wildlingsMusterAtCastle',
            'sourceTile': tn,
            'targetUnits': mus
        }
        json['player_action'] = action
        self.logger.info(json)
        return json