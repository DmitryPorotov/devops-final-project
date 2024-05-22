package fwc

import fwc.game.GameState

trait JsonParsable {
  def fromJson(json: ujson.Value): JsonSerializable
}
