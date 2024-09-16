from DTO.actions.action import ActionResolveCardLion1
from DTO.messages.reply import Reply
from server_module.game_state.game_state import GameState
from server_module.reactions.game_action_reactions.action.clean_up_after_combat_house_card_reaction import \
    CleanUpAfterCombatReactionHouseCard


class ResolveCardLion1Reaction(CleanUpAfterCombatReactionHouseCard):
    def __init__(self, game_state: GameState, reply: Reply[ActionResolveCardLion1]):
        super().__init__(game_state, reply)

    def update_game_state(self):
        super().update_game_state()
        pa: ActionResolveCardLion1 = self._reply['player_action']
        self._game_state.placed_orders.remove_order(pa['tileNumber'])

