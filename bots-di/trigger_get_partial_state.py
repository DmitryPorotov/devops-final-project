import redis
import configs
from DTO.messages.messages import Message


def main():
    r = redis.Redis(host=configs.redis_host, port=configs.redis_port)
    m: Message = {
        "type": "action",
        "action": "get_partial_game_state"
    }
    r.publish("worker1.game3", '{"action":"debug","type":"action"}')

if __name__ == '__main__':
    main()