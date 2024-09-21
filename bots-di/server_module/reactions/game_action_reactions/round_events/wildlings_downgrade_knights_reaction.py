from DTO.actions.events import ActionWildlingsDowngradeKnights
from DTO.messages.reply import Reply
from server_module.game_state.game_state import GameState
from server_module.game_state.house_type import HouseType
from server_module.game_state.military_unit import MilitaryUnit
from server_module.game_state.military_unit_type import MilitaryUnitType
from server_module.reactions.game_action_reactions.base_action_reaction import BaseActionReaction

class WildlingsDowngradeKnightsReaction(BaseActionReaction):
    def __init__(self, game_state: GameState, reply: Reply[ActionWildlingsDowngradeKnights]):
        super().__init__(game_state, reply)

    def update_game_state(self):
        pa: ActionWildlingsDowngradeKnights = self._reply['player_action']
        house = HouseType[pa['houseType'].upper()]
        army = self._game_state.armies[str(pa['tileNumber'])]
        idx = army.index({"type": MilitaryUnitType.KNIGHTS, "house": house})
        army.pop(idx)
        army.append(MilitaryUnit(house, MilitaryUnitType.FOOTMEN))
        self.logger.info(pa)
