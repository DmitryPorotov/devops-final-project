import random
from typing import Optional

from DTO.actions.action import ActionResolveSpecialConsolidatePower
from DTO.messages.messages import MessageGameAction
from DTO.phases.all_phases import SubPhase
from server_module.game_rules.board_tile_type import BoardTileType
from server_module.game_rules.game_rules import GameRules
from server_module.game_state.game_state import GameState
from server_module.game_state.house_type import HouseType
from server_module.game_state.military_unit import MilitaryUnit
from server_module.game_state.military_unit_type import MilitaryUnitType
from server_module.game_state.order import Order
from server_module.game_state.order_type import OrderType
from server_module.reactions.game_phase_reactions.base_phase_reaction import BasePhaseReaction
from utils_ import choose_from_list


class ResolveSpecialConsolidatePowerReaction(BasePhaseReaction):
    def __init__(self, game_id: str, house_type: HouseType, game_state: GameState, game_rules: GameRules, phase: SubPhase):
        super().__init__(game_id, house_type, game_state, game_rules, phase)

    def get_actions(self) -> list[MessageGameAction[ActionResolveSpecialConsolidatePower]]:
        dont_muster = not bool(random.randrange(0, 2))
        tn, order = self.__find_order()
        points_to_muster = self._game_rules.board[int(tn)].mustering_points
        if dont_muster or not points_to_muster:
            return [self._to_json(int(tn))]
        else:
            unit, to_tile, is_upgrade = self.__find_possible_muster_units(points_to_muster, tn)
            return [self._to_json(int(tn), to_tile, unit, is_upgrade)]


    def __find_order(self) -> tuple[str, Order]:
        for tn, o in self._game_state.placed_orders[self._house_type].items():
            if o.order_type is OrderType.CONSOLIDATE_POWER and o.is_star:
                return tn, o


    def __find_possible_muster_units(self, points_to_muster: int, tile_num: str) -> tuple[Optional[MilitaryUnit], Optional[int], bool]:
        units_left_to_muster = self._game_state.armies.get_units_left_to_muster(self._game_rules, self._house_type)
        avail_types = []
        for ut, n in units_left_to_muster:
            if n:
                avail_types.append(ut)

        choices = []
        def muster_for_1_point() -> tuple[Optional[MilitaryUnit], Optional[int], bool]:
            if MilitaryUnitType.FOOTMEN in avail_types:
                choices.append(MilitaryUnitType.FOOTMEN)
            if MilitaryUnitType.SHIPS in avail_types and tile_num != "28" and tile_num != "39":
                choices.append(MilitaryUnitType.SHIPS)
            if not choices:
                return None, None, False
            unit = choose_from_list(choices)
            if unit is MilitaryUnitType.FOOTMEN:
                return MilitaryUnit(self._house_type, unit), None, False
            else:
                tiles = self.__find_sea_neighbours_of_tile(tile_num)
                if not tiles:
                    return None, None, False
                tile = choose_from_list(tiles)
                return MilitaryUnit(self._house_type, MilitaryUnitType.SHIPS), int(tile), False

        def fill_in_choices_for_2_points():
            if MilitaryUnitType.SIEGE_ENGINES in avail_types:
                choices.append(MilitaryUnitType.SIEGE_ENGINES)
            if MilitaryUnitType.KNIGHTS in avail_types:
                choices.extend([MilitaryUnitType.KNIGHTS, MilitaryUnitType.KNIGHTS, MilitaryUnitType.KNIGHTS])

        if points_to_muster == 1:
            if self.__has_footman_to_upgrade_at_tile(tile_num):
                fill_in_choices_for_2_points()
                if not choices:
                    return muster_for_1_point()
                unit = choose_from_list(choices)
                return MilitaryUnit(self._house_type, unit), None, True
            pass
        else:
            fill_in_choices_for_2_points()
            if not choices:
                self.finalizing_move_json = lambda : self._to_json(int(tile_num))
                return muster_for_1_point()
            else:
                unit = choose_from_list(choices)
                return MilitaryUnit(self._house_type, unit), None, False


    def __find_sea_neighbours_of_tile(self, tn: str) -> list[int]:
        tn = int(tn)
        nbs = self._game_rules.board[tn].neighbour_tiles
        candidates = []
        for n in nbs:
            if self._game_rules.board[n].tile_type is BoardTileType.LAND:
                continue
            if n == tn + 1: # is this tile's port
                candidates.append(n)
            elif self._game_rules.board[n].tile_type is BoardTileType.SEA and self.__is_empty_or_friendly_sea(n):
                candidates.append(n)
        return candidates


    def __is_empty_or_friendly_sea(self, tn: int) -> bool:
        tn = str(tn)
        if tn in self._game_state.armies and self._game_state.armies[tn]:
            return self._game_state.armies[tn][0].house == self._house_type
        else:
            return True

    def __has_footman_to_upgrade_at_tile(self, tn: str) -> bool:
        for u in self._game_state.armies[tn]:
            if u.unit_type is MilitaryUnitType.FOOTMEN:
                return True
        return False

    def _to_json(self, from_tile: int, to_tile: int = None , unit: Optional[MilitaryUnit] = None, is_upgrade: bool = False) -> MessageGameAction[ActionResolveSpecialConsolidatePower]:
        json: MessageGameAction[ActionResolveSpecialConsolidatePower] = super()._to_json()
        action: ActionResolveSpecialConsolidatePower = {
            "actionType": 'resolveSpecialConsolidatePower',
            "fromTile": from_tile,
            "houseType": self._house_type,
            'isUpgrade': is_upgrade
        }
        if unit:
            action['unitToMuster'] = unit
        if to_tile is not None:
            action['toTile'] = to_tile
        json['player_action'] = action
        return json