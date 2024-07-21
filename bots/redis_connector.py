import redis

r = redis.Redis(
        host="localhost", port=6379,
    )
pubsub = r.pubsub()
pubsub.subscribe('bot1')
