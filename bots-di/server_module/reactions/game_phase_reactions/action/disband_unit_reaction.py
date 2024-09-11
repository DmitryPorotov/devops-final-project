import random

from DTO.actions.action import ActionDisbandUnitsAfterCombat
from DTO.actions.events import ActionDisbandUnitDueToSupplies
from DTO.messages.messages import MessageGameAction
from DTO.phases.all_phases import SubPhase
from DTO.phases.phases import SubPhaseDisbandUnit
from server_module.game_rules.game_rules import GameRules
from server_module.game_state.game_state import GameState
from server_module.game_state.house_type import HouseType
from server_module.game_state.military_unit import MilitaryUnit
from server_module.game_state.military_unit_type import MilitaryUnitType
from server_module.reactions.game_phase_reactions.base_phase_reaction import BasePhaseReaction
from utils_ import choose_from_list


class DisbandUnitReaction(BasePhaseReaction):
    def __init__(self, game_id: str, house_type: HouseType, game_state: GameState, game_rules: GameRules, phase: SubPhase):
        super().__init__(game_id, house_type, game_state, game_rules, phase)

    def get_actions(self) -> list[MessageGameAction[ActionDisbandUnitsAfterCombat | ActionDisbandUnitDueToSupplies]]:
        combat = self._game_state.combat
        if combat:  # this is after combat
            all_units = list(combat.attacker_army if combat.attacker_house == self._house_type else combat.defender_army)
            unit = choose_from_list(all_units)
            unit.is_defeated = True
            return [self._to_json(unit)]
        else:  # this is after adjusting supplies at round events
            phase: SubPhaseDisbandUnit = self._phase
            biggest_army: tuple[str, list[MilitaryUnit]] = ("", [])
            for tn, army in self._game_state.armies.get_armies_by_house_type_generator(self._house_type):
                if len(army) > 1:
                    commandable_units = [*(mu for mu in army if mu.unit_type not in [MilitaryUnitType.POWER_TOKEN, MilitaryUnitType.GARRISON])]
                    if len(commandable_units) > len(biggest_army[1]):
                        biggest_army = (tn, commandable_units)
            return [self._to_json_supplies(choose_from_list(biggest_army[1]), biggest_army[0], phase['nextStep'])]


    def _to_json(self, unit: MilitaryUnit) -> MessageGameAction[ActionDisbandUnitsAfterCombat]:
        json = super()._to_json()
        action: ActionDisbandUnitsAfterCombat = {
            'houseType': self._house_type,
            'actionType': 'disbandUnitsAfterCombat',
            'unit': unit
        }
        json['player_action'] = action
        self.logger.info(json)
        return json

    def _to_json_supplies(self, unit: MilitaryUnit, from_tile: str, next_step: str) -> MessageGameAction[ActionDisbandUnitDueToSupplies]:
        json = super()._to_json()
        action: ActionDisbandUnitDueToSupplies = {
            'houseType': self._house_type,
            'actionType': 'disbandUnitDueToSupplies',
            'unit': unit,
            'nextStep': next_step,
            'tileNumber': int(from_tile)
        }
        json['player_action'] = action
        self.logger.info(json)
        return json