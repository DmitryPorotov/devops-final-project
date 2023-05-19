package fwc.game.eventsPhase

import fwc.{JsonParsable, JsonSerializable}
import fwc.game.houses.HouseType
import fwc.gameLoading.BoardTile
import ujson.Value
import fwc.game.gameRules

import scala.annotation.tailrec
import scala.collection.mutable

case class UsedMusteringPoints(points: Map[BoardTile, Int] = Map()) extends JsonSerializable {
  def toJson: ujson.Value = {
    ujson.Obj(
      mutable.LinkedHashMap.from(
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
