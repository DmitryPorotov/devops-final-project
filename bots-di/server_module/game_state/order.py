from server_module.game_state.order_type import OrderType


class Order(dict):
    def __init__(
            self,
            order_type: OrderType,
            is_star: bool = False,
            modifier: int = 0,
            **kwargs,
    ):
        super().__init__(**kwargs)
        self.order_type = order_type
        self['type'] = order_type
        self.is_star = is_star
        self['isStar'] = is_star
        self.modifier = modifier
        self['modifier'] = modifier

    def to_json(self):
        o = {
            'type': self.order_type,
        }
        if self.is_star:
            o['isStar'] = self.is_star
        if self.modifier:
            o['modifier'] = self.modifier
        return o

    @classmethod
    def from_json(cls, json):
        return cls(
            OrderType.from_str(json['type']),
            json['isStar'] if 'isStar' in json else False,
            json['modifier'] if 'modifier' in json else 0
        )