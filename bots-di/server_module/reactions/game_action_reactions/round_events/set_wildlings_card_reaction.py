from DTO.actions.all_actions import Action
from DTO.actions.events import ActionSetWildlingsCard
from DTO.messages.reply import Reply
from DTO.phases.phases import SubPhaseWildlingsCard
from server_module.game_state.game_state import GameState
from server_module.game_state.house_card import HouseCard
from server_module.game_state.house_type import HouseType
from server_module.games_data_service import GameHandle
from server_module.reactions.game_action_reactions.base_action_reaction import BaseActionReaction


class SetWildlingsCardReaction(BaseActionReaction):
    def __init__(self, game_state: GameState, reply: Reply[Action]):
        super().__init__(game_state, reply)

    def update_game_state(self, game_data: GameHandle):
        pa: ActionSetWildlingsCard = self._reply['player_action']
        sp: SubPhaseWildlingsCard = self._reply['current_phase']
        l_w_house = HouseType[sp['loserWinnerHouse'].upper()]
        if sp['cardCode'] == 1:
            if sp['isWin']:
                self._game_state.supplies[l_w_house] += 1
                if self._game_state.supplies[l_w_house] > 6:
                    self._game_state.supplies[l_w_house] = 6
            else:
                self._game_state.supplies[l_w_house] -= 2
                if self._game_state.supplies[l_w_house] < 0:
                    self._game_state.supplies[l_w_house] = 0
                for h_ in sp['houseTypes']:
                    h = HouseType[h_.upper()]
                    if h is not l_w_house:
                        self._game_state.supplies[h] -= 1
                        if self._game_state.supplies[h] < 0:
                            self._game_state.supplies[h] = 0

        elif sp['cardCode'] == 2:
            if sp['isWin']:
                if l_w_house in self._game_state.discarded_house_cards:
                    del self._game_state.discarded_house_cards[l_w_house]
            else:
                if len(self._game_state.discarded_house_cards[l_w_house]) == 6:
                    pass  # don't take last card
                else:
                    max_str_cards = []
                    max_str = 0
                    for cc in range(7):
                        if cc not in self._game_state.discarded_house_cards[l_w_house]:
                            strength = HouseCard.from_house_and_code(l_w_house, cc).strength
                            if strength > max_str:
                                max_str = strength
                                if max_str_cards:
                                    max_str_cards.append(cc)
                                else:
                                    max_str_cards = [cc]
                            elif strength == max_str:
                                max_str_cards.append(cc)
                    if len(max_str_cards) + len(self._game_state.discarded_house_cards[l_w_house]) < 7:  # don't take last n cards
                        self._game_state.discarded_house_cards[l_w_house].extend(max_str_cards)
        elif sp['cardCode'] == 4:
            if sp['isWin']:
                self._game_state.power_tokens[l_w_house] += game_data.other['widlings_bids'][l_w_house]
            else:
                self._game_state.power_tokens[l_w_house] = 0
                for h_ in sp['houseTypes']:
                    h = HouseType[h_.upper()]
                    self._game_state.power_tokens[h] -= 2
                    if self._game_state.power_tokens[h] < 0:
                        self._game_state.power_tokens[h] = 0

        self.logger.info(pa)
