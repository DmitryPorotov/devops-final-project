from utils_ import randrange

from DTO.actions.planning import ActionRavenChangeOrder
from DTO.messages.messages import MessageGameAction
from DTO.phases.all_phases import SubPhase
from server_module.game_rules.game_rules import GameRules
from server_module.game_state.game_state import GameState
from server_module.game_state.house_type import HouseType
from server_module.game_state.order import Order
from server_module.reactions.game_phase_reactions.base_phase_reaction import BasePhaseReaction


class RavenChangeOrderReaction(BasePhaseReaction):
    def __init__(self, game_id: str, house_type: HouseType, game_state: GameState, game_rules: GameRules, phase: SubPhase):
        super().__init__(game_id, house_type, game_state, game_rules, phase)

    def get_actions(self) -> list[MessageGameAction[ActionRavenChangeOrder]]:
        if self._house_type in self._game_state.available_orders:
            avail_orders = (o for ot, o in self._game_state.available_orders[self._house_type].items())
            avail_orders_flat = []
            for o in avail_orders:
                avail_orders_flat.extend(o)
            new_idx = randrange(len(avail_orders_flat))
            if len(avail_orders_flat) > 0:
                placed_orders = self._game_state.placed_orders[self._house_type].items()
                random_num = randrange(0, len(placed_orders))
                random_placed_order = None
                for idx, random_placed_order in enumerate(placed_orders):
                    if idx == random_num:
                        break
            return [self._to_json(random_placed_order, avail_orders_flat[new_idx])]
        return []

    def _to_json(self, order_to_replace: tuple[str, Order], replace_with_order: Order) -> MessageGameAction[ActionRavenChangeOrder]:
        json: MessageGameAction[ActionRavenChangeOrder] = super()._to_json()
        action: ActionRavenChangeOrder = {
            "actionType": 'ravenChangeOrder',
            "houseType": self._house_type,
            "order": replace_with_order.to_json(),
            "tileNumber": int(order_to_replace[0])
        }
        json['player_action'] = action
        self.logger.info(json)
        return json
