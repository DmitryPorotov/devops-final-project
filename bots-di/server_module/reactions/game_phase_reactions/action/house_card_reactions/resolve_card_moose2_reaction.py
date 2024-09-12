from utils_ import randrange

from DTO.actions.action import ActionResolveCardMoose2
from DTO.messages.messages import MessageGameAction
from DTO.phases.all_phases import SubPhase
from server_module.game_rules.game_rules import GameRules
from server_module.game_state.game_state import GameState
from server_module.game_state.house_type import HouseType
from server_module.game_state.military_unit import MilitaryUnit
from server_module.game_state.military_unit_type import MilitaryUnitType
from server_module.reactions.game_phase_reactions.base_phase_reaction import BasePhaseReaction


class ResolveCardMoose2Reaction(BasePhaseReaction):
    def __init__(self, game_id: str, house_type: HouseType, game_state: GameState, game_rules: GameRules, phase: SubPhase):
        super().__init__(game_id, house_type, game_state, game_rules, phase)

    def _to_json(self, **kwargs) -> MessageGameAction[ActionResolveCardMoose2]:
        json: MessageGameAction[ActionResolveCardMoose2] = super()._to_json()
        action: ActionResolveCardMoose2 = {
            "houseType": self._house_type,
            "actionType": 'resolveCardMoose2',
            "tileNumber" : self.__get_tile_with_a_footman()
        }
        json['player_action'] = action
        self.logger.info(json)
        return json

    def __get_tile_with_a_footman(self) -> int:
        combat = self._game_state.combat
        me_attacker: bool = combat.attacker_house == self._house_type
        tiles: list[int] = []

        def iter_army(army: list[MilitaryUnit]):
            nonlocal tiles
            for u in army:
                if u.unit_type is MilitaryUnitType.FOOTMEN and u.house is self._house_type:
                    tiles.append(combat.defender_tile_num)

        def iter_support(sup: list[int]):
            if sup:
                for i in sup:
                    iter_army(self._game_state.armies[str(i)])

        if me_attacker:
            iter_army(combat.attacker_army)
            iter_support(combat.attacker_support)
        else:
            iter_army(combat.defender_army)
            iter_support(combat.defender_support)

        return tiles[randrange(len(tiles))]
