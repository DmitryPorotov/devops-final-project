from random import randrange

from DTO.actions.events import ActionRavenChooseTrackBidsOrCollectTaxes
from DTO.messages.messages import MessageGameAction
from DTO.phases.all_phases import SubPhase
from server_module.game_rules.game_rules import GameRules
from server_module.game_state.game_state import GameState
from server_module.game_state.house_type import HouseType
from server_module.game_state.track_type import TrackType
from server_module.reactions.game_phase_reactions.base_phase_reaction import BasePhaseReaction
from server_module.reactions.game_phase_reactions.no_reply_needed_exception import NoReplyNeedException


class RavenChooseTrackBidsOrCollectTaxesReaction(BasePhaseReaction):
    def __init__(self, game_id: str, house_type: HouseType, game_state: GameState, game_rules: GameRules, phase: SubPhase):
        super().__init__(game_id, house_type, game_state, game_rules, phase)

    def get_actions(self) -> list[MessageGameAction[ActionRavenChooseTrackBidsOrCollectTaxes]]:
        if self._house_type != self._game_state.tracks.get_1st_on_track(TrackType.COURT):
            raise NoReplyNeedException()
        choices = ['a', 'b', 'c',]  # a - supply, b - muster, c - nothing
        idx = randrange(len(choices))
        return [self._to_json(choices[idx])]

    def _to_json(self, choice: str) -> MessageGameAction[ActionRavenChooseTrackBidsOrCollectTaxes]:

        json = super()._to_json()
        action: ActionRavenChooseTrackBidsOrCollectTaxes = {
            'houseType': self._house_type,
            'actionType': 'ravenChooseTrackBidsOrCollectTaxes',
            'choice': choice
        }
        json['player_action'] = action
        return json
