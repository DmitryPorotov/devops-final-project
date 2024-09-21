from DTO.actions.events import ActionWildlingsDiscardHouseCard
from DTO.messages.reply import Reply
from server_module.game_state.game_state import GameState
from server_module.game_state.house_type import HouseType
from server_module.reactions.game_action_reactions.base_action_reaction import BaseActionReaction

class WildlingsDiscardHouseCardReaction(BaseActionReaction):
    def __init__(self, game_state: GameState, reply: Reply[ActionWildlingsDiscardHouseCard]):
        super().__init__(game_state, reply)

    def update_game_state(self):
        pa: ActionWildlingsDiscardHouseCard = self._reply['player_action']
        house = HouseType[pa['houseType'].upper()]
        self._game_state.discarded_house_cards[house].append(pa['cardCode'])
        self.logger.info(pa)
