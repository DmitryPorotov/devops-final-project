from DTO.actions.events import ActionWildlingsReturnHouseCard
from DTO.messages.reply import Reply
from server_module.game_state.game_state import GameState
from server_module.game_state.house_type import HouseType
from server_module.reactions.game_action_reactions.base_action_reaction import BaseActionReaction

class WildlingsReturnHouseCardReaction(BaseActionReaction):
    def __init__(self, game_state: GameState, reply: Reply[ActionWildlingsReturnHouseCard]):
        super().__init__(game_state, reply)

    def update_game_state(self):
        pa: ActionWildlingsReturnHouseCard = self._reply['player_action']
        house = HouseType[pa['houseType'].upper()]
        if 'cardCode' in pa and house in self._game_state.discarded_house_cards[house]:
            idx = self._game_state.discarded_house_cards[house].index(pa['cardCode'])
            self._game_state.discarded_house_cards[house].pop(idx)
        self.logger.info(pa)
