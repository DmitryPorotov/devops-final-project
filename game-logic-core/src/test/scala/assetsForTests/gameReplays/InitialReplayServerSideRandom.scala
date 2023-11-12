package assetsForTests.gameReplays

import assetsForTests.gameStates.OneOrderLeftToAdd.gameState
import fwc.game.houses.*
import fwc.{GameSettings, Player}
import fwc.gameSaving.GameReplay

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}

object InitialReplayServerSideRandom extends App {
  val initialGameState = fwc.game.initializeGameState()
  val settings = GameSettings(
    "qwe",
    1,
    false,
    false,
    true,
    Some(Seq(
      Player(
        1, Some(HouseRose)
      ),
      Player(
        2, Some(HouseWolf)
      ),
      Player(
        3, Some(HouseLion)
      ),
      Player(
        4, Some(HouseMoose)
      ),
      Player(
        5, Some(HousePufferfish)
      ),
      Player(
        6, Some(HouseKraken)
      ),
    )),
    None
  )
  val gameReplay = GameReplay(settings, initialGameState.boardCards, initialGameState, Seq())

  Files.write(Paths.get("saves/forIntegration/InitialReplayServerSideRandom.json"), gameReplay.toJsonString.getBytes(StandardCharsets.UTF_8))
}
