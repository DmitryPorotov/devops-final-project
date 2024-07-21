from server.game_state.order_type import OrderType


class Order:
    def __init__(self, order_type: OrderType, is_star: bool = False, modifier: int = 0):
        self.order_type = order_type
        self.is_star = is_star
        self.modifier = modifier

    @classmethod
    def from_json(cls, json):
        return cls(
            OrderType.from_str(json['type']),
            json['isStar'] if 'isStar' in json else False,
            json['modifier'] if 'modifier' in json else 0
        )