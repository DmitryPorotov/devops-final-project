from DTO.actions.planning import ActionRavenChoosePutWildlingsCardOnTopOrBottom
from DTO.messages.reply import Reply
from server_module.game_state.game_state import GameState
from server_module.reactions.game_action_reactions.base_action_reaction import BaseActionReaction


class RavenChoosePutWildlingsCardOnTopOrBottomReaction(BaseActionReaction):
    def __init__(self, game_state: GameState, reply: Reply[ActionRavenChoosePutWildlingsCardOnTopOrBottom]):
        super().__init__(game_state, reply)

    def update_game_state(self):
        pa: ActionRavenChoosePutWildlingsCardOnTopOrBottom = self._reply['player_action']
        self._game_state.dominance_tokens_usage['messengerRaven'] = True
        self.logger.info(pa)
