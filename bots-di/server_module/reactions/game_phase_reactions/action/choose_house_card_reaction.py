import random

from DTO.actions.action import ActionChooseHouseCard
from DTO.messages.messages import MessageGameAction
from DTO.phases.all_phases import SubPhase
from server_module.game_rules.game_rules import GameRules
from server_module.game_state.game_state import GameState
from server_module.game_state.house_type import HouseType
from server_module.reactions.game_phase_reactions.base_phase_reaction import BasePhaseReaction
from utils_ import print_file_lineno_error


class ChooseHouseCardReaction(BasePhaseReaction):
    def __init__(self, game_id: str, house_type: HouseType, game_state: GameState, game_rules: GameRules, phase: SubPhase):
        super().__init__(game_id, house_type, game_state, game_rules, phase)

    def get_actions(self) -> list[MessageGameAction[ActionChooseHouseCard]]:
        unused_cards = []
        if self._house_type not in self._game_state.discarded_house_cards:
            unused_cards = range(7)
        else:
            for i in range(7):
                if i not in self._game_state.discarded_house_cards[self._house_type]:
                    unused_cards.append(i)
        idx = random.randrange(0, len(unused_cards))

        ## note: a code snippet to force a card
        # def has_ships():
        #     if self._game_state.combat.attacker_house == self._house_type:
        #         return self._game_state.combat.attacker_army[0].unit_type is MilitaryUnitType.SHIPS
        #     else:
        #         return self._game_state.combat.defender_army[0].unit_type is MilitaryUnitType.SHIPS
        # if self._house_type is HouseType.KRAKEN and 4 in unused_cards and has_ships():
        #     idx = unused_cards.index(4)

        return [self._to_json(unused_cards[idx])]


    def _to_json(self, card_code: int) -> MessageGameAction[ActionChooseHouseCard]:
        json = super()._to_json()
        action: ActionChooseHouseCard = {
            'houseType': self._house_type,
            'actionType': 'chooseHouseCard',
            'cardCode': card_code,
        }
        json['player_action'] = action
        self.logger.info(json)
        return json
