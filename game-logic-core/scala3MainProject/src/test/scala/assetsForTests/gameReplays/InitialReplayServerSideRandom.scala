package assetsForTests.gameReplays

import assetsForTests.gameStates.OneOrderLeftToAdd.gameState
import fwc.game.houses.*
import fwc.{GameSettings, Player}
import fwc.gameSaving.GameReplay

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}
import java.util.UUID

object InitialReplayServerSideRandom extends App {
  val initialGameState = fwc.game.initializeGameState()
  val settings = GameSettings(
    "qwe",
    UUID.randomUUID(),
    1,
    false,
    false,
    true,
    Some(Seq(
      Player(
        1, "player1", Some(HouseType.Rose)
      ),
      Player(
        2, "player2", Some(HouseType.Wolf)
      ),
      Player(
        3, "player3", Some(HouseType.Lion)
      ),
      Player(
        4, "player4", Some(HouseType.Moose)
      ),
      Player(
        5, "player5", Some(HouseType.PufferFish)
      ),
      Player(
        6, "player6", Some(HouseType.Kraken)
      ),
    )),
    None
  )
  val gameReplay = GameReplay(settings, initialGameState.boardCards, initialGameState, Seq())

  Files.write(Paths.get("saves/forIntegration/InitialReplayServerSideRandom.json"), gameReplay.toJsonString.getBytes(StandardCharsets.UTF_8))
}
