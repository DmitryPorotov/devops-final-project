from dependency_injector import containers, providers
import logging.config

from configs import redis_host, redis_port, my_name
from events_service import EventSourcesService
from redis_service import RedisConnector


from server_module.games_data_service import GamesDataService


class App(containers.DeclarativeContainer):
    logging = providers.Resource(
        logging.config.fileConfig,
        fname="logging.ini",
    )

    game_manager = providers.Singleton(
        GamesDataService
    )

    redis_service = providers.Singleton(
        RedisConnector,
        redis_host=redis_host,
        redis_port=redis_port,
        my_name=my_name
    )
    events = providers.Singleton(
        EventSourcesService,
        redis_service=redis_service
    )
