from DTO.actions.events import ActionWildlingsMusterAtCastle
from DTO.messages.messages import MessageGameAction
from DTO.phases.all_phases import SubPhase
from server_module.game_rules.board_tile import BoardTile
from server_module.game_rules.game_rules import GameRules
from server_module.game_state.game_state import GameState
from server_module.game_state.house_type import HouseType
from server_module.game_state.military_unit import MilitaryUnit
from server_module.reactions.game_phase_reactions.base_phase_reaction import BasePhaseReaction
from server_module.reactions.game_phase_reactions.round_events.muster_reaction import MusterReaction
from utils_ import choose_from_list

class WildlingsMusterAtCastleReaction(MusterReaction):
    def __init__(self, game_id: str, house_type: HouseType, game_state: GameState, game_rules: GameRules, phase: SubPhase):
        super().__init__(game_id, house_type, game_state, game_rules, phase)

    def get_actions(self) -> list[MessageGameAction[ActionWildlingsMusterAtCastle]]:
        my_castles = self.__find_my_castles()
        source_tile: str = choose_from_list([*my_castles.keys()])
        unit, target = super()._choose_unit_to_muster(my_castles[source_tile].mustering_points, int(source_tile))
        return [self._to_json(int(source_tile), [(target, False, unit)])]

    def __find_my_castles(self) -> dict[str, BoardTile]:
        castles = {}
        for tn, army in self._game_state.armies.get_armies_by_house_type_generator(self._house_type):
            if self._game_rules.board[int(tn)].mustering_points > 0:
                castles[tn] = self._game_rules.board[int(tn)]
        return castles

    def _to_json(self, tn: int, mus: list[tuple[int,bool,MilitaryUnit]]) -> MessageGameAction[ActionWildlingsMusterAtCastle]:
        json = BasePhaseReaction._to_json(self)
        action: ActionWildlingsMusterAtCastle = {
            'houseType': self._house_type,
            'actionType': 'wildlingsMusterAtCastle',
            'sourceTile': tn,
            'targetUnits': mus
        }
        json['player_action'] = action
        return json