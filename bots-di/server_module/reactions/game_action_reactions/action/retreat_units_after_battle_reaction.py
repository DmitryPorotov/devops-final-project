from DTO.actions.action import  ActionRetreatUnitsAfterBattle
from DTO.messages.reply import Reply
from server_module.game_state.game_state import GameState
from server_module.reactions.game_action_reactions.base_action_reaction import BaseActionReaction


class RetreatUnitsAfterBattleReaction(BaseActionReaction):
    def __init__(self, game_state: GameState, reply: Reply[ActionRetreatUnitsAfterBattle]):
        super().__init__(game_state, reply)

    def update_game_state(self):
        pa: ActionRetreatUnitsAfterBattle = self._reply['player_action']
        target_tn = str(pa['targetTileNumber'])
        if target_tn not in self._game_state.armies:
            self._game_state.armies[target_tn] = []
        self._game_state.armies[target_tn].extend(self._game_state.combat.defender_army)
        self.logger.info(pa)
