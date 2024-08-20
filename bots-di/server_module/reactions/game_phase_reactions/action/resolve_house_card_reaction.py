from DTO.actions.all_actions import Action
from DTO.messages.messages import MessageGameAction
from DTO.phases.all_phases import SubPhase
from DTO.phases.phases import SubPhaseResolveHouseCard
from server_module.game_rules.game_rules import GameRules
from server_module.game_state.game_state import GameState
from server_module.game_state.house_type import HouseType
from server_module.reactions.game_phase_reactions.action.house_card_reactions.resolve_card_kraken6_reaction import \
    ResolveCardKraken6Reaction
from server_module.reactions.game_phase_reactions.action.house_card_reactions.resolve_card_lion1_reaction import \
    ResolveCardLion1Reaction
from server_module.reactions.game_phase_reactions.action.house_card_reactions.resolve_card_lion5_reaction import \
    ResolveCardLion5Reaction
from server_module.reactions.game_phase_reactions.action.house_card_reactions.resolve_card_moose2_reaction import \
    ResolveCardMoose2Reaction
from server_module.reactions.game_phase_reactions.action.house_card_reactions.resolve_card_moose3_reaction import \
    ResolveCardMoose3Reaction
from server_module.reactions.game_phase_reactions.action.house_card_reactions.resolve_card_pufferfish0_reaction import \
    ResolveCardPufferfish0Reaction
from server_module.reactions.game_phase_reactions.action.house_card_reactions.resolve_card_rose2_reaction import \
    ResolveCardRose2Reaction
from server_module.reactions.game_phase_reactions.action.house_card_reactions.resolve_card_rose4_reaction import \
    ResolveCardRose4Reaction
from server_module.reactions.game_phase_reactions.action.house_card_reactions.resolve_card_wolf0_reaction import \
    ResolveCardWolf0Reaction
from server_module.reactions.game_phase_reactions.base_phase_reaction import BasePhaseReaction


class ResolveHouseCardReaction(BasePhaseReaction):
    def __init__(self, game_id: str, house_type: HouseType, game_state: GameState, game_rules: GameRules, phase: SubPhase):
        super().__init__(game_id, house_type, game_state, game_rules, phase)

    __switch_obj = {
            'lion1': ResolveCardLion1Reaction,
            'lion5': ResolveCardLion5Reaction,
            'moose2': ResolveCardMoose2Reaction,
            'moose3': ResolveCardMoose3Reaction,
            'wolf0': ResolveCardWolf0Reaction,
            'kraken6': ResolveCardKraken6Reaction,
            'pufferfish0': ResolveCardPufferfish0Reaction,
            'rose2': ResolveCardRose2Reaction,
            'rose4': ResolveCardRose4Reaction,
        }


    def get_actions(self) -> list[MessageGameAction[Action]]:
        phase: SubPhaseResolveHouseCard = self._phase
        try:
            card_reaction_cls = ResolveHouseCardReaction.__switch_obj["{}{}".format(phase['houseType'],phase['cardCode'])]
        except KeyError as e:
            raise Exception("Unknown house card {} - {}".format(phase['houseType'], phase['cardCode']))
        return [self._to_json(card_reaction_cls)]

    def _to_json(self, card_reaction_cls: type[BasePhaseReaction]) -> MessageGameAction[Action]:
        inst = card_reaction_cls(self._game_id, self._house_type, self._game_state, self._game_rules, self._phase)
        return inst._to_json()