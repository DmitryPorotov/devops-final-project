from DTO.actions.action import ActionResolveSpecialConsolidatePower
from DTO.messages.reply import Reply
from server_module.game_state.game_state import GameState
from server_module.game_state.house_type import HouseType
from server_module.game_state.military_unit import MilitaryUnit
from server_module.game_state.military_unit_type import MilitaryUnitType
from server_module.reactions.game_action_reactions.round_events.power_tokens_change_generic_reaction import \
    PowerTokensChangeGenericReaction


class ResolveSpecialConsolidatePowerReaction(PowerTokensChangeGenericReaction):
    def __init__(self, game_state: GameState, reply: Reply[ActionResolveSpecialConsolidatePower]):
        super().__init__(game_state, reply)

    def update_game_state(self):
        super().update_game_state()
        pa: ActionResolveSpecialConsolidatePower = self._reply['player_action']
        house = HouseType[pa['houseType'].upper()]
        from_tile = str(pa['fromTile'])
        if 'unitToMuster' in pa and pa['unitToMuster'] is not None:
            mil_unit = MilitaryUnit.from_json(pa['unitToMuster'])
            if 'toTile' in pa and pa['toTile'] is not None:
                if str(pa['toTile']) not in self._game_state.armies:
                    self._game_state.armies[str(pa['toTile'])].append(mil_unit)
            else:
                if 'isUpgrade' in pa and pa['isUpgrade']:
                    idx = self._game_state.armies[str(pa['fromTile'])].index({'type': 'footmen', 'house': pa['houseType']})
                    self._game_state.armies[from_tile].pop(idx)
                self._game_state.armies[from_tile].append(mil_unit)
            if from_tile not in self._game_state.used_mustering_points:
                self._game_state.used_mustering_points[from_tile] = 0
            self._game_state.used_mustering_points[from_tile] += (1 if ('isUpgrade' in pa and pa['isUpgrade']) or mil_unit.unit_type in [MilitaryUnitType.FOOTMEN, MilitaryUnitType.SHIPS] else 2)
        else:
            pass # self._game_state.power_tokens[house] += 1

        self._game_state.placed_orders.remove_order(pa['fromTile'], house)