from DTO.actions.action import ActionLeavePowerTokenAtTile
from DTO.messages.reply import Reply
from server_module.game_state.armies import Armies
from server_module.game_state.combat import Combat
from server_module.game_state.discarded_house_cards import DiscardedHouseCards
from server_module.game_state.game_state import GameState
from server_module.game_state.house_type import HouseType
from server_module.game_state.military_unit import MilitaryUnit
from server_module.game_state.military_unit_type import MilitaryUnitType
from server_module.game_state.placed_orders import PlacedOrders
from server_module.game_state.power_tokens import PowerTokens
from server_module.reactions.game_action_reactions.base_action_reaction import BaseActionReaction


class LeavePowerTokenAtTileReaction(BaseActionReaction):
    def __init__(self, game_state: GameState, reply: Reply[ActionLeavePowerTokenAtTile]):
        super().__init__(game_state, reply)

    def update_game_state(self):
        house = HouseType[self._reply['player_action']['houseType'].upper()]
        pa: ActionLeavePowerTokenAtTile = self._reply['player_action']
        if 'state' in pa:
            state_json = pa['state']
            self._game_state.armies = self._game_state['armies'] = Armies(**state_json['armies'])
            self._game_state.placed_orders = self._game_state['placedOrders'] = PlacedOrders(**state_json['placedOrders'])
            self._game_state.discarded_house_cards = self._game_state['discardedHouseCards'] = DiscardedHouseCards(**state_json['discardedHouseCards'])
            self._game_state.power_tokens = self._game_state['powerTokens'] = PowerTokens(**state_json['powerTokens'])
        else:
            if pa['doLeave']:
                self._game_state.power_tokens[house] -= 1
                self._game_state.armies[str(pa['tileNumber'])] = [
                    MilitaryUnit(house, MilitaryUnitType.POWER_TOKEN)
                ]
                self._game_state['armies'] = self._game_state.armies
            if 'combat' in self._reply:
                self._game_state.combat = Combat.from_json(self._reply['combat'])
