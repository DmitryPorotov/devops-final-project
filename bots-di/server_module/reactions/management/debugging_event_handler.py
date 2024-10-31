import json
from datetime import datetime

from dependency_injector.wiring import Provide, inject

from containers_module import App
from events_service import EventSourcesService
from redis_service import RedisConnector
from server_module.games_data_service import GamesDataService


class DebuggingEventHandler:
    @inject
    def __init__(
            self,
            events_service: EventSourcesService = Provide[App.events],
            games_data: GamesDataService = Provide[App.game_service],
            redis: RedisConnector = Provide[App.redis_service]
    ):
        self.redis = redis
        self._events_service = events_service
        self._games_data = games_data
        self._events_service.debugging_event.subscribe(on_next=self._on_debug_event)


    def _on_debug_event(self, msg):
        # note: put break point here
        data = self._games_data
        state = data._games["3"].state
        # self._dump_to_file(state)
        a = 0

    @staticmethod
    def _dump_to_file(data):
        j = json.dumps(data, indent=2, sort_keys=True)
        name = "dump-{}.json".format(datetime.now())
        f = open("./state_dumps/{}".format(name), "w")
        f.write(j)
        f.close()