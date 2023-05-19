package fwc.gameLoading

case class RoundEventCard(
                           code: Int, 
                           title: String, 
                           text: String,
                           wildlings: Int
                         ) extends CardTrait {
  override def getCode: Int = code
}
