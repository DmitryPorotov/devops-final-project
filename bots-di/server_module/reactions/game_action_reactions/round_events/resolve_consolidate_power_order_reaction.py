from DTO.actions.action import ActionResolveConsolidatePowerOrder
from DTO.messages.reply import Reply
from server_module.game_state.game_state import GameState
from server_module.reactions.game_action_reactions.round_events.power_tokens_change_generic_reaction import \
    PowerTokensChangeGenericReaction


class ResolveConsolidatePowerOrderReaction(PowerTokensChangeGenericReaction):
    def __init__(self, game_state: GameState, reply: Reply[ActionResolveConsolidatePowerOrder]):
        super().__init__(game_state, reply)

