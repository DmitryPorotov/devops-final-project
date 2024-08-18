from dependency_injector.wiring import inject, Provide

from DTO.messages.messages import Message
from containers_module import App
from events_service import EventSourcesService
from server_module.games_data_service import GamesDataService
from server_module.reactions.game_phase_reactions.planning.add_order_reaction import AddOrderReaction


class NewOrResetGame:
    @inject
    def __init__(self,
                 events_service: EventSourcesService = Provide[App.events],
                 games_data: GamesDataService = Provide[App.game_manager],):
        self._games_data = games_data
        self._events_service = events_service
        self._events_service.game_management_event_sources.reset_game.subscribe(on_next=self.on_new_reset)

    def on_new_reset(self, msg):
        message, channel = msg  # type: Message, str
        AddOrderReaction.delete_game(message['gameId'])
        self._games_data.delete_game(message['gameId'])
