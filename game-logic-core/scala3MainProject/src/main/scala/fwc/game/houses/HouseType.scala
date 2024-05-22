package fwc.game.houses

sealed trait HouseType {

  protected def concreteObj: HouseType
  def isHigherOnTrack(track: Seq[HouseType])(houseType: HouseType): Boolean =
    val idx1 = track.indexOf(concreteObj)
    val idx2 = track.indexOf(houseType)
    if idx1 < 0 || idx2 < 0
    then throw new RuntimeException("One of the houses is not on the track")
    idx1 < idx2
}

case object HouseWolf extends HouseType {
  override def toString = "wolf"

  override protected def concreteObj: HouseType = this
}

case object HouseMoose extends HouseType {
  override def toString = "moose"

  override protected def concreteObj: HouseType = this
}

case object HousePufferfish extends HouseType {
  override def toString = "pufferfish"
  override protected def concreteObj: HouseType = this
}

case object HouseKraken extends HouseType {
  override def toString = "kraken"
  override protected def concreteObj: HouseType = this
}

case object HouseRose extends HouseType {
  override def toString = "rose"
  override protected def concreteObj: HouseType = this
}

case object HouseLion extends HouseType {
  override def toString = "lion"
  override protected def concreteObj: HouseType = this
}

case object HouseNeutral extends HouseType {
  override def toString = "neutral"
  override protected def concreteObj: HouseType = ???
  override def isHigherOnTrack(track: Seq[HouseType])(houseType: HouseType): Boolean = ???
}

object HouseType {
  def fromString(str: String): HouseType = {
    str match
      case "lion" => HouseLion
      case "kraken" => HouseKraken
      case "pufferfish" => HousePufferfish
      case "wolf" => HouseWolf
      case "moose" => HouseMoose
      case "rose" => HouseRose
      case "neutral" => HouseNeutral
      case _ => throw new RuntimeException(s"Unknown house $str")
  }

  def getSeqOfAll: Seq[HouseType] = Seq(
    HouseMoose,
    HouseKraken,
    HouseLion,
    HouseRose,
    HouseWolf,
    HousePufferfish
  )
}
