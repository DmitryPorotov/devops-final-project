package fwc.game.board

import fwc.{JsonParsable, JsonSerializable}
import fwc.game.houses.{HouseNeutral, HouseType}
import fwc.gameLoading.BoardStart
import ujson.Value

import scala.annotation.{tailrec, targetName}

case class Tracks(private val tracks: Map[TrackType, Seq[HouseType]]) extends JsonSerializable {
  def toJson: ujson.Value = ujson.Obj(
      upickle.core.LinkedHashMap(
        tracks.map((trackType, housesSeq: Seq[HouseType]) => {
          trackType.toString -> ujson.Value(
            housesSeq.map((ht: HouseType) => ujson.Str(ht.toString))
          )
        })
      )
    )

  export tracks.{apply, foldLeft}
  
  @targetName("updated")
  def +(kv: (TrackType, Seq[HouseType])): Tracks = copy(tracks + kv)

  def throneOwner: HouseType = tracks(TrackThrone).head

  def steelBladeOwner: HouseType = tracks(TrackFiefdoms).head

  def ravenOwner: HouseType = tracks(TrackCourt).head
  
  def setHouseHighestOnTrack(houseType: HouseType, trackType: TrackType): Tracks = {
    copy(
      tracks + (trackType -> (tracks(trackType).filter(_ != houseType) prepended houseType))
    )
  }

  def setHouseLowestOnTrack(houseType: HouseType, trackType: TrackType): Tracks = {
    copy(
      tracks + (trackType -> (tracks(trackType).filter(_ != houseType) appended houseType))
    )
  }

  def getHighestTracks(houseType: HouseType): Seq[TrackType] = {
    tracks.foldLeft((Int.MaxValue, Map[TrackType, Int]()))(
      (acc: (Int, Map[TrackType, Int]), cur: (TrackType, Seq[HouseType])) =>
        val indexOnTrack = cur._2.indexOf(houseType)
        if indexOnTrack <= acc._1
        then acc.copy(
          _1 = indexOnTrack,
          _2 = acc._2.filter(_._2 == indexOnTrack) + (cur._1 -> indexOnTrack)
        )
        else acc
    )._2.keys.toSeq
  }
  
  def reduce2PositionsOnTrack(trackType: TrackType, houseType: HouseType): Tracks = {
    @tailrec
    def reduce2(track: Seq[HouseType], acc: Seq[HouseType] = Seq()): Seq[HouseType] = {
      if track.isEmpty
      then acc
      else
        if track.head == houseType
        then
          if track.tail.size >= 2  
          then (acc :+ track.tail.head :+ track.tail.tail.head :+ track.head) ++ track.tail.tail.tail
          else if track.tail.size == 1
            then (acc :+ track.tail.head :+ track.head) ++ track.tail.tail
            else acc :+ track.head
        else reduce2(track.tail, acc :+ track.head)
    }
    val reduced = reduce2(tracks(trackType))
    copy(tracks + (trackType -> reduced))
  }
}

object Tracks extends JsonParsable {
  def initialize(boardStart: Seq[BoardStart]): Tracks = {
    val throneTrack = boardStart.sortWith((a, b) => a.tracks.throne < b.tracks.throne)
      .map(_.house)
      .filter(_ != HouseNeutral)

    val fiefdomsTrack = boardStart.sortWith((a,b) => a.tracks.fiefdoms < b.tracks.fiefdoms)
      .map(_.house)
      .filter(_ != HouseNeutral)
    
    val courtTrack = boardStart.sortWith((a,b) => a.tracks.court < b.tracks.court)
      .map(_.house)
      .filter(_ != HouseNeutral)
    
    Tracks(Map(
      TrackThrone -> throneTrack,
      TrackFiefdoms -> fiefdomsTrack,
      TrackCourt -> courtTrack
    ))
  }

  override def fromJson(json: Value): Tracks = {
    Tracks(
      json.obj.map((trackType, houses: Value) => {
        TrackType.fromString(trackType) -> houses.arr.map(house => HouseType.fromString(house.str)).toSeq
      }).toMap
    )
  }
}
