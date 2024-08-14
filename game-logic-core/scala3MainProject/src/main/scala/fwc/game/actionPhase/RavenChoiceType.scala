package fwc.game.actionPhase {

  import fwc.game.FWCException

  sealed trait RavenChoiceType
  
  object RavenChoiceType {
    case object Nothing extends RavenChoiceType {
      override def toString: String = "nothing"
    }

    case object ChangeOrder extends RavenChoiceType {
      override def toString: String = "changeOrder"
    }

    case object LookAtWildlingsCard extends RavenChoiceType {
      override def toString: String = "lookAtWildlingsCard"
    }
    
    def fromString(str: String): RavenChoiceType = str match
      case "nothing" => Nothing
      case "changeOrder" => ChangeOrder
      case "lookAtWildlingsCard" => LookAtWildlingsCard
      case s => throw new FWCException(s"Unknown raven choice $s")
  }
}