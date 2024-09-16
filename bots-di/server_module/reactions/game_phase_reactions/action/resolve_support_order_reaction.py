import random

from DTO.actions.action import ActionResolveSupportOrder
from DTO.messages.messages import MessageGameAction
from DTO.phases.all_phases import SubPhase
from DTO.phases.phases import SubPhaseResolveSupportOrder
from server_module.game_rules.game_rules import GameRules
from server_module.game_state.game_state import GameState
from server_module.game_state.house_type import HouseType
from server_module.reactions.game_phase_reactions.base_phase_reaction import BasePhaseReaction


class ResolveSupportOrderReaction(BasePhaseReaction):
    def __init__(self, game_id: str, house_type: HouseType, game_state: GameState, game_rules: GameRules, phase: SubPhase):
        super().__init__(game_id, house_type, game_state, game_rules, phase)

    def get_actions(self) -> list[MessageGameAction[ActionResolveSupportOrder]]:
        combat = self._game_state.combat
        phase: SubPhaseResolveSupportOrder = self._phase
        if self._house_type is combat.attacker_house or self._house_type is combat.defender_house:
            return [self._to_json(self._house_type, phase['tilesNumbers'])]
        else:
            to_house = combat.defender_house if random.randrange(2) else combat.attacker_house
            if to_house is HouseType.NEUTRAL:
                to_house = combat.attacker_house
            return [self._to_json(to_house, phase['tilesNumbers'])]


    def _to_json(self, to_house_type: HouseType, tile_nums: list[int]) -> MessageGameAction[ActionResolveSupportOrder]:
        json = super()._to_json()
        action: ActionResolveSupportOrder = {
            'fromHouseType': self._house_type,
            'actionType': 'resolveSupportOrder',
            'toHouseType': to_house_type,
            'tileNumbers': tile_nums
        }
        json['player_action'] = action
        self.logger.info(json)
        return json
