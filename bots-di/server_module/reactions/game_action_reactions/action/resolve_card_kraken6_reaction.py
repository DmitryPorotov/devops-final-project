from DTO.actions.action import ActionResolveCardKraken6
from DTO.messages.reply import Reply
from server_module.game_state.game_state import GameState
from server_module.game_state.house_type import HouseType
from server_module.reactions.game_action_reactions.base_action_reaction import BaseActionReaction


class ResolveCardKraken6Reaction(BaseActionReaction):
    def __init__(self, game_state: GameState, reply: Reply[ActionResolveCardKraken6]):
        super().__init__(game_state, reply)

    def update_game_state(self):
        pa: ActionResolveCardKraken6 = self._reply['player_action']
        if pa['newCardCode'] is not None and pa['newCardCode'] >= 0:
            if HouseType.KRAKEN not in self._game_state.discarded_house_cards:
                self._game_state.discarded_house_cards[HouseType.KRAKEN] = []
            self._game_state.discarded_house_cards[HouseType.KRAKEN].append(6)
            self._game_state.power_tokens[HouseType.KRAKEN] -= 2
        self.logger.info(pa)