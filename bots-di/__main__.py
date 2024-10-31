from dependency_injector.wiring import inject, Provide

from containers_module import App
import init_observers
from redis_service import RedisConnector


@inject
def main(redis_service: RedisConnector = Provide[App.redis_service]):
    init_observers.init()

    redis_service.start()


if __name__ == "__main__":
    print('Starting bots...')
    application = App()
    application.init_resources()
    application.wire(modules=[
        __name__,
        *init_observers.imports()
    ])
    main()
