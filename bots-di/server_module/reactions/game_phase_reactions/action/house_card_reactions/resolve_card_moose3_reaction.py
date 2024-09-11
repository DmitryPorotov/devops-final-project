from random import randrange

from DTO.actions.action import ActionResolveCardMoose3
from DTO.messages.messages import MessageGameAction
from DTO.phases.all_phases import SubPhase
from server_module.game_rules.game_rules import GameRules
from server_module.game_state.game_state import GameState
from server_module.game_state.house_type import HouseType
from server_module.reactions.game_phase_reactions.base_phase_reaction import BasePhaseReaction


class ResolveCardMoose3Reaction(BasePhaseReaction):
    def __init__(self, game_id: str, house_type: HouseType, game_state: GameState, game_rules: GameRules, phase: SubPhase):
        super().__init__(game_id, house_type, game_state, game_rules, phase)

    def _to_json(self, **kwargs) -> MessageGameAction[ActionResolveCardMoose3]:
        json: MessageGameAction[ActionResolveCardMoose3] = super()._to_json()
        action: ActionResolveCardMoose3 = {
            "houseType": self._house_type,
            "actionType": 'resolveCardMoose3',
            "cardCode":  self.__get_card_code()
        }
        json['player_action'] = action
        self.logger.info(json)
        return json

    def __get_card_code(self) -> int:
        if not randrange(5):
            return -1
        combat = self._game_state.combat
        discarded = list(self._game_state.discarded_house_cards[self._house_type] if self._house_type in self._game_state.discarded_house_cards else [])
        opponent_card_code = combat.attacker_card.code if combat.defender_house is HouseType.MOOSE else combat.defender_card.code
        available = [*(x for x in range(7) if x not in discarded and x != opponent_card_code)]
        if len(available) > 1:
            idx = randrange(len(available))
            return available[idx]
        else:
            return -1
