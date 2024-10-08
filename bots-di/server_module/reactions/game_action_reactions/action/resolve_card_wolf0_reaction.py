from DTO.actions.action import ActionResolveCardWolf0
from DTO.messages.reply import Reply
from server_module.game_state.game_state import GameState
from server_module.game_state.house_type import HouseType
from server_module.reactions.game_action_reactions.action.clean_up_after_combat_house_card_reaction import \
    CleanUpAfterCombatReactionHouseCard


class ResolveCardWolf0Reaction(CleanUpAfterCombatReactionHouseCard):
    def __init__(self, game_state: GameState, reply: Reply[ActionResolveCardWolf0]):
        super().__init__(game_state, reply)

    def update_game_state(self):
        pa: ActionResolveCardWolf0 = self._reply['player_action']
        army = self._game_state.combat.attacker_army if self._game_state.combat.defender_house is HouseType.WOLF else self._game_state.combat.defender_army
        for mu in army:
            mu.is_defeated = True
        tile_num = str(pa['targetTileNumber'])
        if tile_num not in self._game_state.armies:
            self._game_state.armies[tile_num] = []
        self._game_state.armies[tile_num].extend(army)
        super().update_game_state()


