package fwc.game.phases
import fwc.game.board.TrackType
import ujson.Value

trait SubPhaseMultipleHousesTracks(trackType: TrackType) extends SubPhaseMultipleHouses {
  override def toJson: ujson.Value = {
    val json = super.toJson
    json.obj.addOne("trackType" -> trackType.toString)
    json
  }
}
