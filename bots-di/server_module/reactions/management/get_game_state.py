from dependency_injector.wiring import Provide, inject
from reactivex import operators as op

from containers_module import App
from events_service import EventSourcesService


class GetGameState:
    @inject
    def __init__(self, events_service: EventSourcesService = Provide[App.events]):
        def on_game_state(state):
            a = 0
            pass
        events_service.game_management_event_sources.react_to_game.pipe(
            op.filter(lambda t: t[0]['type'] == 'action' and t[0]['action'] == 'get_game_state')
        ).subscribe(on_next=on_game_state)
