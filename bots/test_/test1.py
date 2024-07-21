import unittest

from server.game_rules.game_rules import GameRules
from server.game_state.game_state import GameState
from test_.redis_connector_for_test import RedisConnectorForTest, RedisMessage
from time import sleep
import json


class Test1(unittest.TestCase):
    def test_add_order(self):
        con = RedisConnectorForTest()
        con.start(self._on_message)
        # con.send('{"userId":-1,"gameId":"2","action":"get_status"}')
        con.send('{"userId":-1,"gameId":"2","action":"create_game","isRandomHouses":false}')
        con.send('{"userId":-1,"gameId":"2","action":"join_game","name":"bot1","joinAs":"moose"}')
        con.send('{"userId":-1,"gameId":"2","action":"get_game_state"}')
        sleep(5000)

    def _on_message(self, message: RedisMessage):
        if message['type'] == 'pmessage':
            obj = json.loads(message['data'])
            if obj['action'] == 'get_game_state':
                gs = GameState.from_json(obj['gameState'])
                gr = GameRules.from_json(obj['gameRules'])
                a = 0

