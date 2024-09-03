from DTO.actions.action import ActionKillUnitsAfterBattle
from DTO.messages.messages import MessageGameAction
from DTO.phases.all_phases import SubPhase
from server_module.game_rules.game_rules import GameRules
from server_module.game_state.game_state import GameState
from server_module.game_state.house_type import HouseType
from server_module.game_state.military_unit import MilitaryUnit
from server_module.reactions.game_phase_reactions.base_phase_reaction import BasePhaseReaction


class KillUnitsAfterBattleReaction(BasePhaseReaction):
    def __init__(self, game_id: str, house_type: HouseType, game_state: GameState, game_rules: GameRules, phase: SubPhase):
        super().__init__(game_id, house_type, game_state, game_rules, phase)

    def get_actions(self) -> list[MessageGameAction[ActionKillUnitsAfterBattle]]:
        raise Exception('Unimplemented!')

    def _to_json(self, units: list[MilitaryUnit]) -> MessageGameAction[ActionKillUnitsAfterBattle]:
        json = super()._to_json()
        action: ActionKillUnitsAfterBattle = {
            'houseType': self._house_type,
            'actionType': 'killUnitsAfterBattle',
            'units': units
        }
        json['player_action'] = action
        return json
