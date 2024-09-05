from random import randrange

from DTO.actions.events import ActionResolveTiesAfterBiddingOnTracks
from DTO.messages.messages import MessageGameAction
from DTO.phases.all_phases import SubPhase
from DTO.phases.phases import SubPhaseResolveTiesAfterBiddingOnTracks
from server_module.game_rules.game_rules import GameRules
from server_module.game_state.game_state import GameState
from server_module.game_state.house_type import HouseType
from server_module.reactions.game_phase_reactions.base_phase_reaction import BasePhaseReaction


class ResolveTiesAfterBiddingOnTracksReaction(BasePhaseReaction):
    def __init__(self, game_id: str, house_type: HouseType, game_state: GameState, game_rules: GameRules, phase: SubPhase):
        super().__init__(game_id, house_type, game_state, game_rules, phase)

    def get_actions(self) -> list[MessageGameAction[ActionResolveTiesAfterBiddingOnTracks]]:
        bids = self._game_state.bids
        res_tuples = sorted(bids.items(),key=lambda kvp: kvp[1],reverse=True)
        res_tuples1 = [*(t[0] for t in res_tuples)]
        return [self._to_json(res_tuples1)]

    def _to_json(self, resolution: list[str]) -> MessageGameAction[ActionResolveTiesAfterBiddingOnTracks]:
        json = super()._to_json()
        phase: SubPhaseResolveTiesAfterBiddingOnTracks = self._phase
        action: ActionResolveTiesAfterBiddingOnTracks = {
            'houseType': self._house_type,
            'actionType': 'resolveTiesAfterBiddingOnTracks',
            'resolution': resolution,
            'trackType': phase['trackType']
        }
        json['player_action'] = action
        return json