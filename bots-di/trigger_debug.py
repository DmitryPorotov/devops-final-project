import redis
import configs


def main():
    r = redis.Redis(host=configs.redis_host, port=configs.redis_port)
    r.publish(configs.my_name + ".debug", '{"action":"debug","type":"action"}')

if __name__ == '__main__':
    main()