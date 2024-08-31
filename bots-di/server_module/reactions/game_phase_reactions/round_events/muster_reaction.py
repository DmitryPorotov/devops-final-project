from random import randrange
from typing import Optional

from DTO.actions.events import ActionMuster
from DTO.messages.messages import MessageGameAction
from DTO.phases.all_phases import SubPhase
from server_module.game_rules.board_tile import BoardTile
from server_module.game_rules.board_tile_type import BoardTileType
from server_module.game_rules.game_rules import GameRules
from server_module.game_state.game_state import GameState
from server_module.game_state.house_type import HouseType
from server_module.game_state.military_unit import MilitaryUnit
from server_module.game_state.military_unit_type import MilitaryUnitType
from server_module.reactions.game_phase_reactions.base_phase_reaction import BasePhaseReaction


class MusterReaction(BasePhaseReaction):
    def __init__(self, game_id: str, house_type: HouseType, game_state: GameState, game_rules: GameRules, phase: SubPhase):
        super().__init__(game_id, house_type, game_state, game_rules, phase)

    def get_actions(self) -> list[MessageGameAction[ActionMuster]]:
        my_armies = self._game_state.armies.get_armies_by_house_type(self._house_type)
        musterable_tiles: dict[str, BoardTile] = {}
        for tn, t in self._game_rules.board.get_castle_tiles().items():
            if (tn in my_armies and t.mustering_points > 0) or (t.home_of == self._house_type and tn not in self._game_state.armies):
                musterable_tiles[tn] = t
        if len(musterable_tiles) > 1:
            idx = randrange(len(musterable_tiles))
            tns = [*(tn for tn, bt in musterable_tiles.items())]
            tile_num = int(tns[idx])
            tile = self._game_rules.board[tile_num]
        elif len(musterable_tiles) == 1:
            for tn, t in musterable_tiles.items():
                tile_num = int(tn)
                tile = t
        else:
            raise Exception('Should not get here!')
        mu, to_tile = self.__choose_unit_to_muster(tile.mustering_points, tile_num)
        return [self._to_json(mu, tile_num, to_tile)]

    __units_types = [MilitaryUnitType.FOOTMEN, MilitaryUnitType.FOOTMEN, MilitaryUnitType.SHIPS, MilitaryUnitType.KNIGHTS, MilitaryUnitType.SIEGE_ENGINES]
    def __choose_unit_to_muster(self, avail_points: int, from_tile_num: int) -> "tuple[MilitaryUnit, int]":
        if avail_points > 1:
            idx = randrange(len(self.__units_types))
        else:
            idx = randrange(3)
        return MilitaryUnit(
            self._house_type,
            self.__units_types[idx],
        ), self.__choose_tile(self.__units_types[idx], from_tile_num)

    def __choose_tile(self, unit_type: MilitaryUnitType, from_tile_num: int):
        if unit_type is MilitaryUnitType.SHIPS:
            neighbours = self._game_rules.board[from_tile_num].neighbour_tiles
            seas = []
            for tn in neighbours:
                if self._game_rules.board[tn].tile_type is BoardTileType.SEA or self._game_rules.board[tn].tile_type is BoardTileType.PORT:
                    seas.append(tn)
            idx = randrange(len(seas))
            return seas[idx]
        else:
            return from_tile_num

    def _to_json(self, unit_to_muster: MilitaryUnit, from_tile: int, to_tile: Optional[int] = None) -> MessageGameAction[ActionMuster]:
        json = super()._to_json()
        action: ActionMuster = {
            'houseType': self._house_type,
            'actionType': 'muster',
            'unitToMuster': unit_to_muster,
            'fromTile': from_tile,
        }
        if to_tile is not None:
            action['toTile'] = to_tile

        json['player_action'] = action
        return json
