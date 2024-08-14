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

object HouseType {
  case object Wolf extends HouseType {
    override def toString = "wolf"

    override protected def concreteObj: HouseType = this
  }

  case object Moose extends HouseType {
    override def toString = "moose"

    override protected def concreteObj: HouseType = this
  }

  case object PufferFish extends HouseType {
    override def toString = "pufferfish"

    override protected def concreteObj: HouseType = this
  }

  case object Kraken extends HouseType {
    override def toString = "kraken"

    override protected def concreteObj: HouseType = this
  }

  case object Rose extends HouseType {
    override def toString = "rose"

    override protected def concreteObj: HouseType = this
  }

  case object Lion extends HouseType {
    override def toString = "lion"

    override protected def concreteObj: HouseType = this
  }

  case object Neutral extends HouseType {
    override def toString = "neutral"

    override protected def concreteObj: HouseType = ???

    override def isHigherOnTrack(track: Seq[HouseType])(houseType: HouseType): Boolean = ???
  }
  
  def fromString(str: String): HouseType = {
    str match
      case "lion" => Lion
      case "kraken" => Kraken
      case "pufferfish" => PufferFish
      case "wolf" => Wolf
      case "moose" => Moose
      case "rose" => Rose
      case "neutral" => Neutral
      case _ => throw new RuntimeException(s"Unknown house $str")
  }

  def getSeqOfAll: Seq[HouseType] = Seq(
    Moose,
    Kraken,
    Lion,
    Rose,
    Wolf,
    PufferFish
  )
}
