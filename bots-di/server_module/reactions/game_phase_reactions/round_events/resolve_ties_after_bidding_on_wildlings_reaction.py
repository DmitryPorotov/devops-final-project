from random import randrange

from DTO.actions.events import ActionResolveTiesAfterBiddingOnWildlings
from DTO.messages.messages import MessageGameAction
from DTO.phases.all_phases import SubPhase
from DTO.phases.phases import SubPhaseResolveTiesAfterBiddingOnWildlings
from server_module.game_rules.game_rules import GameRules
from server_module.game_state.game_state import GameState
from server_module.game_state.house_type import HouseType
from server_module.reactions.game_phase_reactions.base_phase_reaction import BasePhaseReaction


class ResolveTiesAfterBiddingOnWildlingsReaction(BasePhaseReaction):
    def __init__(self, game_id: str, house_type: HouseType, game_state: GameState, game_rules: GameRules, phase: SubPhase):
        super().__init__(game_id, house_type, game_state, game_rules, phase)

    def get_actions(self) -> list[MessageGameAction[ActionResolveTiesAfterBiddingOnWildlings]]:
        phase: SubPhaseResolveTiesAfterBiddingOnWildlings = self._phase
        idx = randrange(len(phase['houseTypes']))
        return [self._to_json(phase['houseTypes'][idx])]

    def _to_json(self, winner_looser: HouseType) -> MessageGameAction[ActionResolveTiesAfterBiddingOnWildlings]:
        json = super()._to_json()
        action: ActionResolveTiesAfterBiddingOnWildlings = {
            'houseType': self._house_type,
            'actionType': 'resolveTiesAfterBiddingOnWildlings',
            'winnerLoser': winner_looser
        }
        json['player_action'] = action
        self.logger.info(json)
        return json