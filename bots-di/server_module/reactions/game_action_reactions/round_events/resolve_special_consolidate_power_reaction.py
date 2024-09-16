from DTO.actions.action import ActionResolveSpecialConsolidatePower
from DTO.messages.reply import Reply
from server_module.game_state.game_state import GameState
from server_module.game_state.house_type import HouseType
from server_module.game_state.military_unit import MilitaryUnit
from server_module.reactions.game_action_reactions.base_action_reaction import BaseActionReaction


class ResolveSpecialConsolidatePowerReaction(BaseActionReaction):
    def __init__(self, game_state: GameState, reply: Reply[ActionResolveSpecialConsolidatePower]):
        super().__init__(game_state, reply)

    def update_game_state(self):
        pa: ActionResolveSpecialConsolidatePower = self._reply['player_action']
        house = HouseType[pa['houseType'].upper()]
        if 'unitToMuster' in pa and pa['unitToMuster'] is not None:
            if 'toTile' in pa and pa['toTile'] is not None:
                if str(pa['toTile']) not in self._game_state.armies:
                    self._game_state.armies[str(pa['toTile'])].append(MilitaryUnit.from_json(pa['unitToMuster']))
            else:
                if 'isUpgrade' in pa and pa['isUpgrade']:
                    idx = self._game_state.armies[str(pa['fromTile'])].index({'type': 'footmen', 'house': pa['houseType']})
                    self._game_state.armies[str(pa['fromTile'])].pop(idx)
                self._game_state.armies[str(pa['fromTile'])].append(MilitaryUnit.from_json(pa['unitToMuster']))
        else:
            self._game_state.power_tokens[house] += 1

        self._game_state.placed_orders.remove_order(pa['fromTile'], house)

        self.logger.info(pa)