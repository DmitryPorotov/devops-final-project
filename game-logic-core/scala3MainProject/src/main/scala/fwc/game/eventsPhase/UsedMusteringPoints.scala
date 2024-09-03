package fwc.game.eventsPhase

import fwc.{JsonParsable, JsonSerializable}
import fwc.gameLoading.BoardTile
import ujson.Value
import fwc.game.gameRules


case class UsedMusteringPoints(points: Map[BoardTile, Int] = Map()) extends JsonSerializable {
  export points.{apply, getOrElse, map}
  def toJson: ujson.Value = {
    ujson.Obj(
      upickle.core.LinkedHashMap(
        points.map((boardTile:BoardTile, numUsed)=>{
          boardTile.toNumberString -> ujson.Num(numUsed)
        })
      )
    )
  }
  
}

object UsedMusteringPoints extends JsonParsable {
  override def fromJson(json: Value): UsedMusteringPoints = {
    UsedMusteringPoints(
      json.obj.map((tileNum: String, points: Value) =>
        gameRules.board(tileNum.toInt) -> points.num.toInt
      ).toMap
    )
  }
}
