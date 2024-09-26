from DTO.actions.action import ActionResolveCardMoose2
from DTO.messages.reply import Reply
from server_module.game_state.game_state import GameState
from server_module.game_state.military_unit_type import MilitaryUnitType
from server_module.reactions.game_action_reactions.action.clean_up_after_combat_house_card_reaction import \
    CleanUpAfterCombatReactionHouseCard


class ResolveCardMoose2Reaction(CleanUpAfterCombatReactionHouseCard):
    def __init__(self, game_state: GameState, reply: Reply[ActionResolveCardMoose2]):
        super().__init__(game_state, reply)

    def update_game_state(self):
        pa: ActionResolveCardMoose2 = self._reply['player_action']
        if pa['tileNumber'] is not None:
            tile_num = str(pa['tileNumber'])
            for unit in self._game_state.armies[tile_num]:
                if unit.unit_type == MilitaryUnitType.FOOTMEN:
                    unit.unit_type = MilitaryUnitType.KNIGHTS
                    break
        super().update_game_state()