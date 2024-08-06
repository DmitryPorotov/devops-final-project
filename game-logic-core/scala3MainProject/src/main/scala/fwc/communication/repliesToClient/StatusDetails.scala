package fwc.communication.repliesToClient

import fwc.JsonSerializable
import fwc.game.phases.SubPhase
import fwc.gameSaving.GameReplay
import ujson.Value

case class StatusDetails(
                          gameReplay: GameReplay,
                        ) extends JsonSerializable {
  def toJson: Value = {
    ujson.Obj(
      "roundCounter" -> gameReplay.currentGameState.roundCounter,
      "gameSettings" -> gameReplay.gameSettings.toJson,
      "subPhase" -> gameReplay.currentGameState.subPhase.toJson,
    )
  }
}