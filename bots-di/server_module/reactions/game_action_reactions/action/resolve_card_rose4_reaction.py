from DTO.actions.action import ActionResolveCardRose4
from DTO.messages.reply import Reply
from server_module.game_state.game_state import GameState
from server_module.game_state.house_type import HouseType
from server_module.reactions.game_action_reactions.base_action_reaction import BaseActionReaction


class ResolveCardRose4Reaction(BaseActionReaction):
    def __init__(self, game_state: GameState, reply: Reply[ActionResolveCardRose4]):
        super().__init__(game_state, reply)

    def update_game_state(self):
        pa: ActionResolveCardRose4 = self._reply['player_action']
        if pa['tileNumber'] is not None and pa['tileNumber'] >= 0:
            opponent = self._game_state.combat.attacker_house if self._game_state.combat.defender_house == HouseType.ROSE else self._game_state.combat.defender_house
            self._game_state.placed_orders.remove_order(pa['tileNumber'], opponent)
        self.logger.info(pa)