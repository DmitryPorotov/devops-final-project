package fwc.gameSaving.actions

import fwc.game.GameState
import fwc.{JsonParsable, JsonSerializable}
import ujson.Value

trait JsonParsableAction {
  def fromJson(gameState: GameState, json : Value): Action
}
