from utils_ import randrange

from DTO.actions.action import ActionResolveCardWolf0
from DTO.messages.messages import MessageGameAction
from DTO.phases.all_phases import SubPhase
from server_module.game_rules.board_tile_type import BoardTileType
from server_module.game_rules.game_rules import GameRules
from server_module.game_state.game_state import GameState
from server_module.game_state.house_type import HouseType
from server_module.reactions.game_phase_reactions.action.common import find_reachable_targets
from server_module.reactions.game_phase_reactions.base_phase_reaction import BasePhaseReaction


class ResolveCardWolf0Reaction(BasePhaseReaction):
    def __init__(self, game_id: str, house_type: HouseType, game_state: GameState, game_rules: GameRules, phase: SubPhase):
        super().__init__(game_id, house_type, game_state, game_rules, phase)

    def _to_json(self) -> MessageGameAction[ActionResolveCardWolf0]:
        json: MessageGameAction[ActionResolveCardWolf0] = super()._to_json()
        action: ActionResolveCardWolf0 = {
            "houseType": self._house_type,
            "actionType": 'resolveCardWolf0',
            "targetTileNumber": self.__get_tile_num()
        }
        json['player_action'] = action
        self.logger.info(json)
        return json

    def __get_tile_num(self) -> int:
        combat = self._game_state.combat
        targets = self._game_rules.board[combat.defender_tile_num].neighbour_tiles if self._game_rules.board[combat.defender_tile_num].tile_type is BoardTileType.SEA \
            else find_reachable_targets(self._game_rules, self._game_state, self._house_type, self._game_rules.board[combat.defender_tile_num])
        return targets[randrange(len(targets))]