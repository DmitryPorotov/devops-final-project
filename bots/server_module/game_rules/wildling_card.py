class WildlingCard:
    def __init__(
            self,
            code: int,
            title: str,
            wildling_victory_lowest_bidder_text: str,
            wildling_victory_everyone_else_text: str,
            player_victory_text: str,
    ):
        self.code = code
        self.title = title
        self.wildling_victory_lowest_bidder_text = wildling_victory_lowest_bidder_text
        self.wildling_victory_everyone_else_text = wildling_victory_everyone_else_text
        self.player_victory_text = player_victory_text

    @classmethod
    def from_json(cls, json):
        return cls(
            json['code'],
            json['title'],
            json['wildlingVictoryLowestBidderText'],
            json['wildlingVictoryEveryoneElseText'],
            json['playerVictoryText']
        )
