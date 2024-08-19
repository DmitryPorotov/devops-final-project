import random

from DTO.actions.action import ActionLeavePowerTokenAtTile
from DTO.messages.messages import MessageGameAction
from server_module.game_rules.game_rules import GameRules
from server_module.game_state.game_state import GameState
from server_module.game_state.house_type import HouseType
from server_module.reactions.game_phase_reactions.base_phase_reaction import BasePhaseReaction


class LeavePowerTokenAtTileReaction(BasePhaseReaction):
    def __init__(self, game_id: str, house_type: HouseType, game_state: GameState, game_rules: GameRules):
        super().__init__(game_id, house_type, game_state, game_rules)

    def get_actions(self) -> list[MessageGameAction[ActionLeavePowerTokenAtTile]]:
        try:
            do_leave = bool(random.randrange(0, 10))
            return [self._to_json(do_leave)]
        except Exception as e:
            print("module " + __name__ + " error " + str(e))

    def _to_json(self, do_leave: bool) -> MessageGameAction[ActionLeavePowerTokenAtTile]:
        json = super()._to_json()
        action: ActionLeavePowerTokenAtTile = {
            'houseType': self._house_type,
            'actionType': 'leavePowerTokenAtTile',
            'doLeave': do_leave,
        }
        json['player_action'] = action
        return json
