package fwc.game.actionPhase

import fwc.{JsonParsable, JsonSerializable}
import fwc.game.board.DominanceTokenType
import ujson.Value

import scala.annotation.targetName

case class DominanceTokensUsage(usage: Map[DominanceTokenType, Boolean] = Map(
  DominanceTokenType.ValyrianSword->false,
  DominanceTokenType.MessengerRaven->false
)) extends JsonSerializable {
  def toJson: ujson.Value = {
    ujson.Obj(
      upickle.core.LinkedHashMap(
        usage.map((dominanceTokenType, isUsed) => {
          dominanceTokenType.toString -> ujson.Bool(isUsed)
        })
      )
    )
  }

  export usage.{apply, map}

  @targetName("updated")
  def +(kv: (DominanceTokenType, Boolean)): DominanceTokensUsage = copy(usage + kv)
}

object DominanceTokensUsage extends JsonParsable {
  override def fromJson(json: Value): DominanceTokensUsage = {
    DominanceTokensUsage(
      json.obj.map((token, isUsed: Value) =>
        DominanceTokenType.fromString(token) -> isUsed.bool
      ).toMap
    )
  }
}