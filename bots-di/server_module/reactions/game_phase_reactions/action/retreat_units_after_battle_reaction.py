from dependency_injector.wiring import inject, Provide

from containers_module import App
from server_module.games_data_service import GamesDataService
from utils_ import randrange

from DTO.actions.action import ActionRetreatUnitsAfterBattle
from DTO.messages.messages import MessageGameAction
from DTO.phases.all_phases import SubPhase
from server_module.game_rules.board_tile_type import BoardTileType
from server_module.game_rules.game_rules import GameRules
from server_module.game_state.game_state import GameState
from server_module.game_state.house_type import HouseType
from server_module.reactions.game_phase_reactions.action.common import find_reachable_targets
from server_module.reactions.game_phase_reactions.base_phase_reaction import BasePhaseReaction


class RetreatUnitsAfterBattleReaction(BasePhaseReaction):
    def __init__(self, game_id: str, house_type: HouseType, game_state: GameState, game_rules: GameRules, phase: SubPhase):
        super().__init__(game_id, house_type, game_state, game_rules, phase)

    @inject
    def get_actions(self, game_service: GamesDataService = Provide[App.game_service]) -> list[MessageGameAction[ActionRetreatUnitsAfterBattle]]:
        candidate_tile_nums = self.__get_candidates()
        # todo: try to avoid supply problems, go to empty tiles first maybe
        idx = randrange(len(candidate_tile_nums))
        tile_num = candidate_tile_nums[idx]
        game_service.get_game(self._game_id).other['last_tile_retreated_to'] = tile_num
        return [self._to_json(tile_num)]

    def __get_candidates(self) -> list[int]:
        combat = self._game_state.combat
        source_tile = self._game_rules.board[combat.defender_tile_num]
        if source_tile.tile_type is BoardTileType.SEA:
            valid_to_retreat = [*(tn for tn in source_tile.neighbour_tiles if self.__is_valid_retreat_for_ship(tn))]
            try:
                idx = valid_to_retreat.index(combat.attacker_tile_num)
                valid_to_retreat.pop(idx)
            except ValueError:
                pass
            return valid_to_retreat
        else:
            candidate_tile_nums = find_reachable_targets(self._game_rules, self._game_state, self._house_type, source_tile, [combat.defender_tile_num])
            for i, tn in enumerate(candidate_tile_nums):
                if str(tn) not in self._game_state.armies or (self._game_state.armies[str(tn)] and
                        self._game_state.armies[str(tn)][0].house == self._house_type):
                    continue
                else:
                    candidate_tile_nums.pop(i)
            return candidate_tile_nums

    def __is_valid_retreat_for_ship(self, tile_num: int) -> bool:
        tile = self._game_rules.board[tile_num]
        if tile.tile_type is BoardTileType.SEA:
            if str(tile_num) in self._game_state.armies:
                if self._game_state.armies[str(tile_num)]:
                    if self._game_state.armies[str(tile_num)][0].house == self._house_type:
                        return True
                    else:
                        return False
                else:
                    return True
            else:
                return True
        elif tile.tile_type is BoardTileType.PORT:
            if str(tile_num - 1) in self._game_state.armies and self._game_state.armies[str(tile_num - 1)] and \
                    self._game_state.armies[str(tile_num - 1)][0].house == self._house_type:
                return True
            else:
                return False
        else:
            return False



    def _to_json(self, tile_num: int) ->  MessageGameAction[ActionRetreatUnitsAfterBattle]:
        json: MessageGameAction[ActionRetreatUnitsAfterBattle] = super()._to_json()
        pa: ActionRetreatUnitsAfterBattle = {
            'actionType': 'retreatUnitsAfterBattle',
            'houseType': self._house_type,
            'targetTileNumber': tile_num
        }
        json['player_action'] = pa
        self.logger.info(json)
        return json