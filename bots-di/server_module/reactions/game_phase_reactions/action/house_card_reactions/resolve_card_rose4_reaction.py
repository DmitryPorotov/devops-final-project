from random import randrange

from DTO.actions.action import ActionResolveCardRose4
from DTO.messages.messages import MessageGameAction
from DTO.phases.all_phases import SubPhase
from server_module.game_rules.game_rules import GameRules
from server_module.game_state.game_state import GameState
from server_module.game_state.house_type import HouseType
from server_module.reactions.game_phase_reactions.base_phase_reaction import BasePhaseReaction


class ResolveCardRose4Reaction(BasePhaseReaction):
    def __init__(self, game_id: str, house_type: HouseType, game_state: GameState, game_rules: GameRules, phase: SubPhase):
        super().__init__(game_id, house_type, game_state, game_rules, phase)

    def _to_json(self) -> MessageGameAction[ActionResolveCardRose4]:
        json: MessageGameAction[ActionResolveCardRose4] = super()._to_json()
        action: ActionResolveCardRose4 = {
            "houseType": self._house_type,
            "actionType": 'resolveCardRose4',
            "tileNumber": self.__get_tile_num() if randrange(5) else -1
        }
        json['player_action'] = action
        return json

    def __get_tile_num(self) -> int:
        combat = self._game_state.combat
        neighbors = [*(x for x in self._game_rules.board[combat.defender_tile_num].neighbour_tiles if x != combat.attacker_tile_num)]
        opponent_house: HouseType = combat.defender_house if self._house_type == combat.attacker_house else combat.attacker_house
        candidate_orders = [*(int(tn) for tn, o in self._game_state.placed_orders[opponent_house].items() if int(tn) in neighbors)]
        if candidate_orders:
            idx = randrange(len(candidate_orders))
            return candidate_orders[idx]
        else:
            return -1
