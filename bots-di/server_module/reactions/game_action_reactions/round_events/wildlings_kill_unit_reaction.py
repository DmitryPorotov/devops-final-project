from DTO.actions.events import ActionCollectTaxes, ActionWildlingsKillUnit
from DTO.messages.reply import Reply
from server_module.game_state.game_state import GameState
from server_module.reactions.game_action_reactions.base_action_reaction import BaseActionReaction

class WildlingsKillUnitReaction(BaseActionReaction):
    def __init__(self, game_state: GameState, reply: Reply[ActionCollectTaxes]):
        super().__init__(game_state, reply)

    def update_game_state(self):
        pa: ActionWildlingsKillUnit = self._reply['player_action']
        army = self._game_state.armies[str(pa['tileNumber'])]
        idx = army.index({'house': pa['unit']['house'], 'type': pa['unit']['type']})
        del army[idx]
        if not army:
            del self._game_state.armies[str(pa['tileNumber'])]

        self.logger.info(pa)
