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
        1, HouseRose
      ),
      Player(
        2, HouseWolf
      ),
      Player(
        3, HouseLion
      ),
      Player(
        4, HouseMoose
      ),
      Player(
        5, HousePufferfish
      ),
      Player(
        6, HouseKraken
      ),
    )),
    None
  )
  val gameReplay = GameReplay(settings, initialGameState.boardCards, initialGameState, Seq())

  Files.write(Paths.get("saves/forIntegration/InitialReplayServerSideRandom.json"), gameReplay.toJsonString.getBytes(StandardCharsets.UTF_8))
}
