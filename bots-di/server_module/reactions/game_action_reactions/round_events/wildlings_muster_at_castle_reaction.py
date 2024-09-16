from DTO.actions.events import ActionWildlingsMusterAtCastle
from DTO.messages.reply import Reply
from server_module.game_state.game_state import GameState
from server_module.game_state.house_type import HouseType
from server_module.game_state.military_unit import MilitaryUnit
from server_module.game_state.track_type import TrackType
from server_module.reactions.game_action_reactions.base_action_reaction import BaseActionReaction

class WildlingsMusterAtCastleReaction(BaseActionReaction):
    def __init__(self, game_state: GameState, reply: Reply[ActionWildlingsMusterAtCastle]):
        super().__init__(game_state, reply)

    def update_game_state(self):
        pa: ActionWildlingsMusterAtCastle = self._reply['player_action']
        if 'targetUnits' in pa and pa['targetUnits']:
            for targets in pa['targetUnits']:
                tn = str(targets[0])
                is_upgrade = targets[1]
                unit = MilitaryUnit.from_json(targets[2])
                if is_upgrade:
                    idx = self._game_state.armies[tn].index({"type": "footmen", "house": pa['houseType']})
                    self._game_state.armies[tn].pop(idx)
                if tn not in self._game_state.armies:
                    self._game_state.armies[tn] = []
                self._game_state.armies[tn].append(unit)

        self.logger.info(pa)
