import random

from DTO.actions.action import ActionResolveRaidOrder
from DTO.messages.messages import MessageGameAction
from DTO.phases.all_phases import SubPhase
from server_module.game_rules.board_tile_type import BoardTileType
from server_module.game_rules.game_rules import GameRules
from server_module.game_state.game_state import GameState
from server_module.game_state.house_type import HouseType
from server_module.game_state.order import Order
from server_module.game_state.order_type import OrderType
from server_module.reactions.game_phase_reactions.base_phase_reaction import BasePhaseReaction


class ResolveRaidOrderReaction(BasePhaseReaction):
    def __init__(self, game_id: str, house_type: HouseType, game_state: GameState, game_rules: GameRules, phase: SubPhase):
        super().__init__(game_id, house_type, game_state, game_rules, phase)

    def get_actions(self) -> list[MessageGameAction[ActionResolveRaidOrder]]:
        order_to_use = self.__choose_source_order()
        source = int(order_to_use[0])
        dont_raid = not bool(random.randrange(0, 4))  # note 75% chance to do raid
        if dont_raid:
            return [self._to_json(source, source)]
        target = self.__choose_target_order(source, order_to_use[1].is_star)
        return [self._to_json(source, target)]

    def __choose_source_order(self) -> tuple[str, Order]:
        raid_orders = []
        for tn, o in self._game_state.placed_orders[self._house_type].items():
            if o.order_type is OrderType.RAID:
                raid_orders.append((tn, o))
        idx = random.randrange(len(raid_orders))
        return raid_orders[idx]

    def __choose_target_order(self, source: int, is_star: bool) -> int:

        source_tile = self._game_rules.board[source]
        neighbours = source_tile.neighbour_tiles
        target_candidates = []
        for h, os in self._game_state.placed_orders.items():
            if h != self._house_type:
                for tn, o in os.items():
                    if int(tn) in neighbours and (ResolveRaidOrderReaction.__is_raidable_order(o.order_type, is_star, source_tile.tile_type, self._game_rules.board[int(tn)].tile_type)):
                        target_candidates.append((int(tn), o))
        if len(target_candidates) == 0:
            return source
        else:
            idx = random.randrange(len(target_candidates))
            return target_candidates[idx][0]


    @staticmethod
    def __is_raidable_order(ot: OrderType,  is_star: bool, source_tile_type: BoardTileType, target_tile_type: BoardTileType):
        return (ot is OrderType.RAID or ot is OrderType.SUPPORT or ot is OrderType.CONSOLIDATE_POWER or (ot is OrderType.DEFEND and is_star))\
            and (source_tile_type is not BoardTileType.LAND or target_tile_type is BoardTileType.LAND)

    def _to_json(self, source: int, target: int) -> MessageGameAction[ActionResolveRaidOrder]:
        json = super()._to_json()
        action: ActionResolveRaidOrder = {
            'houseType': self._house_type,
            'sourceTileNumber': source,
            'targetTileNumber': target,
            'actionType': 'resolveRaidOrder'
        }
        json['player_action'] = action
        self.logger.info(json)
        return json
