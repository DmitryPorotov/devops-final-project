import random

from DTO.actions.action import ActionResolveMarchOrder
from DTO.messages.messages import MessageGameAction
from DTO.phases.all_phases import SubPhase
from server_module.game_rules.board_tile import BoardTile
from server_module.game_rules.board_tile_type import BoardTileType
from server_module.game_rules.game_rules import GameRules
from server_module.game_state.game_state import GameState
from server_module.game_state.house_type import HouseType
from server_module.game_state.military_unit import MilitaryUnit
from server_module.game_state.order_type import OrderType
from server_module.reactions.game_phase_reactions.base_phase_reaction import BasePhaseReaction
from utils_ import print_file_lineno_error
from server_module.reactions.game_phase_reactions.action.common import find_reachable_targets


class ResolveMarchOrderReaction(BasePhaseReaction):
    def __init__(self, game_id: str, house_type: HouseType, game_state: GameState, game_rules: GameRules, phase: SubPhase):
        super().__init__(game_id, house_type, game_state, game_rules, phase)

    def get_actions(self) -> list[MessageGameAction[ActionResolveMarchOrder]]:
        try:
            source = self.__choose_source_order()
            targets = {}
            dont_march = not bool(random.randrange(0, 500))  # note 75% chance to do march
            if dont_march:
                return [self._to_json(source, targets)]
            targets = self.__choose_target_tiles(source)
            self._game_state.placed_orders.remove_order(source, self._house_type)
            return [self._to_json(source, targets)]
        except Exception as e:
            print_file_lineno_error(e)

    def __choose_source_order(self) -> int:
        march_orders = []
        for tn, o in self._game_state.placed_orders[self._house_type].items():
            if o.order_type is OrderType.MARCH:
                march_orders.append(tn)
        random.shuffle(march_orders)
        return int(march_orders[0])

    def __choose_target_tiles(self, source: int) -> dict[int, list[MilitaryUnit]]:
        # note: I'll do only 1 target for now
        # todo: check supplies (maybe I should do a retry strategy instead)
        try:
            source_tile = self._game_rules.board[source]
            potential_targets = source_tile.neighbour_tiles if source_tile.tile_type is BoardTileType.SEA or source_tile.tile_type is BoardTileType.PORT else\
                find_reachable_targets(self._game_rules, self._game_state, self._house_type, source_tile, [source])

            if source_tile.tile_type is BoardTileType.LAND:
                potential_targets = potential_targets[1:]

            if source_tile.tile_type is BoardTileType.SEA:
                potential_targets = self.__filter_out_land(potential_targets)

            if len(potential_targets) == 0:
                return { }

            idx = random.randrange(len(potential_targets))
            armies_at_source = list(self._game_state.armies[str(source)])
            armies_at_source = self.__filter_out_unmusterable(armies_at_source)
            num_to_send = random.randrange(1, len(armies_at_source) + 1)
            random.shuffle(armies_at_source)
            return {
                potential_targets[idx]: armies_at_source[:num_to_send]
            }
        except Exception as e:
            print_file_lineno_error(e)

    @staticmethod
    def __filter_out_unmusterable(armies: list[MilitaryUnit]):
        tmp = []
        for u in armies:
            if u.unit_type.get_mustering_points() > 0:
                tmp.append(u)
        return tmp

    def __filter_out_land(self, potential_targets: list[int]) -> list[int]:
        tmp = []
        for tn in potential_targets:
            if self._game_rules.board[tn].tile_type is BoardTileType.SEA:
                tmp.append(tn)
            elif self._game_rules.board[tn].tile_type is BoardTileType.PORT\
                    and str(tn - 1) in self._game_state.armies\
                    and len(self._game_state.armies[str(tn-1)])\
                    and self._game_state.armies[str(tn-1)][0].house == self._house_type:
                tmp.append(tn)

        return tmp

    def _to_json(self, source: int, targets:  dict[int, list[MilitaryUnit]]) -> MessageGameAction[ActionResolveMarchOrder]:
        json = super()._to_json()
        action: ActionResolveMarchOrder = {
            'houseType': self._house_type,
            'sourceTileNumber': source,
            'targets': targets,
            'actionType': 'resolveMarchOrder'
        }
        json['player_action'] = action
        return json
