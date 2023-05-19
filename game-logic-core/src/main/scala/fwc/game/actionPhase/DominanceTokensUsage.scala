package fwc.game.actionPhase

import fwc.{JsonParsable, JsonSerializable}
import fwc.game.board.{DominanceTokenMessengerRaven, DominanceTokenType, DominanceTokenValyrianSword}
import ujson.Value

import scala.annotation.{tailrec, targetName}
import scala.collection.mutable

case class DominanceTokensUsage(usage: Map[DominanceTokenType, Boolean] = Map(
  DominanceTokenValyrianSword->false,
  DominanceTokenMessengerRaven->false
)) extends JsonSerializable {
  def toJson: ujson.Value = {
    ujson.Obj(
      mutable.LinkedHashMap.from(
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