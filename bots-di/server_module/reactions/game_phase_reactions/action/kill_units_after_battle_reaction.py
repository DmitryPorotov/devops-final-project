import random

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
        combat = self._game_state.combat
        num_units_to_kill = combat.combat_outcome.attacker_units_to_kill if combat.attacker_house == self._house_type else combat.combat_outcome.defender_units_to_kill
        all_units = list(combat.attacker_army if combat.attacker_house == self._house_type else combat.defender_army)
        random.shuffle(all_units)
        to_kill = all_units[:num_units_to_kill]
        return [self._to_json(to_kill)]

    def _to_json(self, units: list[MilitaryUnit]) -> MessageGameAction[ActionKillUnitsAfterBattle]:
        json = super()._to_json()
        action: ActionKillUnitsAfterBattle = {
            'houseType': self._house_type,
            'actionType': 'killUnitsAfterBattle',
            'units': units
        }
        json['player_action'] = action
        self.logger.info(json)
        return json
