from DTO.actions.events import ActionWildlingsUpgradeKnights
from DTO.messages.reply import Reply
from server_module.game_state.game_state import GameState
from server_module.game_state.house_type import HouseType
from server_module.game_state.military_unit import MilitaryUnit
from server_module.game_state.military_unit_type import MilitaryUnitType
from server_module.reactions.game_action_reactions.base_action_reaction import BaseActionReaction

class WildlingsUpgradeKnightsReaction(BaseActionReaction):
    def __init__(self, game_state: GameState, reply: Reply[ActionWildlingsUpgradeKnights]):
        super().__init__(game_state, reply)

    def update_game_state(self):
        pa: ActionWildlingsUpgradeKnights = self._reply['player_action']
        house = HouseType[pa['houseType'].upper()]
        self.__upgrade_to_knight(str(pa['tileNumber1']), house)
        if 'tileNumber2' in pa and pa['tileNumber2'] is not None and pa['tileNumber2'] >= 0:
            self.__upgrade_to_knight(str(pa['tileNumber2']), house)
        self.logger.info(pa)

    def __upgrade_to_knight(self, tile_num: str, house):
        idx = self._game_state.armies[tile_num].index({"type": MilitaryUnitType.FOOTMEN, "house": house})
        self._game_state.armies[str(tile_num)].pop(idx)
        self._game_state.armies[str(tile_num)].append(MilitaryUnit(house, MilitaryUnitType.KNIGHTS))