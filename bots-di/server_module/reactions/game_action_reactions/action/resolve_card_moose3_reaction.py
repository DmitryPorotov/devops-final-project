from DTO.actions.action import  ActionResolveCardMoose3
from DTO.messages.reply import Reply
from server_module.game_state.game_state import GameState
from server_module.game_state.house_type import HouseType
from server_module.reactions.game_action_reactions.action.clean_up_after_combat_house_card_reaction import \
    CleanUpAfterCombatReactionHouseCard


class ResolveCardMoose3Reaction(CleanUpAfterCombatReactionHouseCard):
    def __init__(self, game_state: GameState, reply: Reply[ActionResolveCardMoose3]):
        super().__init__(game_state, reply)

    def update_game_state(self):
        pa: ActionResolveCardMoose3 = self._reply['player_action']
        if pa['cardCode'] is not None and pa['cardCode'] != -1:
            op_house = HouseType[pa['opponentHouseType'].upper()]
            if op_house not in self._game_state.discarded_house_cards:
                self._game_state.discarded_house_cards[op_house] = []
            self._game_state.discarded_house_cards[op_house].append(pa['cardCode'])
        super().update_game_state()