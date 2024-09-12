from DTO.actions.planning import ActionRavenChooseChangeOrderOrLookAtWildlingCard
from DTO.messages.reply import Reply
from server_module.game_state.game_state import GameState
from server_module.reactions.game_action_reactions.base_action_reaction import BaseActionReaction


class RavenChooseChangeOrderOrLookAtWildlingCardReaction(BaseActionReaction):
    def __init__(self, game_state: GameState, reply: Reply[ActionRavenChooseChangeOrderOrLookAtWildlingCard]):
        super().__init__(game_state, reply)

    def update_game_state(self):
        pa: ActionRavenChooseChangeOrderOrLookAtWildlingCard = self._reply['player_action']
        self._game_state.dominance_tokens_usage['messengerRaven'] = pa['ravenChoice'] != 'nothing'
