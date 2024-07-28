import unittest
import json

from server_module.game_rules.game_rules import GameRules
from server_module.game_state.game_state import GameState
from server_module.game_state.house_type import HouseType
from server_module.game_state.order import Order
from server_module.game_state.order_type import OrderType
from test_.redis_connector_for_test import RedisConnectorForTest, RedisMessage
from time import sleep


class Test1(unittest.TestCase):
    def test_add_order(self):
        con = RedisConnectorForTest()
        con.start(self._on_message)
        # con.send('{"userId":-1,"gameId":"2","action":"get_status"}')
        con.send('{"userId":-1,"gameId":"2","action":"create_game","isRandomHouses":false}')
        con.send('{"userId":-1,"gameId":"2","action":"join_game","name":"bot1","joinAs":"moose"}')
        con.send('{"userId":-1,"gameId":"2","action":"get_game_state"}')
        sleep(500)

    def _on_message(self, message: RedisMessage):
        if message['type'] == 'pmessage':
            obj = json.loads(message['data'])
            if obj['action'] == 'get_game_state':
                gs = GameState.from_json(obj['gameState'])
                gr = GameRules.from_json(obj['gameRules'])
                gs.placed_orders.place_order(HouseType.WOLF, 3, Order(OrderType.CONSOLIDATE_POWER), 1)
                gs.available_orders.build_from_placed_orders(gr, gs.placed_orders)
