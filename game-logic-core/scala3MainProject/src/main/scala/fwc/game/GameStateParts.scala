package fwc.game

enum GameStateParts(val string: String) {
  override def toString: String = string
  case SubPhase extends GameStateParts("subPhase")
  case Armies extends GameStateParts("armies")
  case Tracks extends GameStateParts("tracks")

  case Supplies extends GameStateParts("supplies")
  case DiscardedHouseCards extends GameStateParts("discardedHouseCards")
  case PowerTokens extends GameStateParts("powerTokens")
  case DominanceTokensUsage extends GameStateParts("dominanceTokensUsage")
  case UsedMusteringPoints extends GameStateParts("usedMusteringPoints")
  case WildlingCounter extends GameStateParts("wildlingCounter")
  case WildlingsStartedFrom12Points extends GameStateParts("wildlingsStartedFrom12Points")
  case RoundCounter extends GameStateParts("roundCounter")

  case BoardCards extends GameStateParts("boardCards")
  case PlacedOrders extends GameStateParts("placedOrders")
  case AvailableOrders extends GameStateParts("availableOrders")
  case Bids extends GameStateParts("bids")
  case Combat extends GameStateParts("combat")

}