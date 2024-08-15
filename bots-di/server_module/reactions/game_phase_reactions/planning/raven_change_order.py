from DTO.messages.messages import MessageGameAction
from server_module.game_rules.game_rules import GameRules
from server_module.game_state.game_state import GameState
from server_module.game_state.house_type import HouseType
from server_module.reactions.game_phase_reactions.base_phase_reaction import BasePhaseReaction


class RavenChangeOrder(BasePhaseReaction):
    def __init__(self, game_id: str, house_type: HouseType, game_state: GameState, game_rules: GameRules):
        super().__init__(game_id, house_type, game_state, game_rules)

    def get_actions(self) -> list[MessageGameAction]:
        if self._house_type in self._game_state.available_orders:
            orders = (o for ot, o in self._game_state.available_orders[self._house_type].items())
            orders_flat = []
            for o in orders:
                orders_flat.extend(o)
            if len(orders_flat) > 0:
                pass

        return self._to_json()

    def _to_json(self):
        pass