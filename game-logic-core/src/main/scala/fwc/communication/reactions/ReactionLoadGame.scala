package fwc.communication.reactions

import fwc.game.FWCException
import fwc.gameSaving.GameReplay

import java.nio.file.{Files, Paths}

object ReactionLoadGame {
  def apply(userId: Int, saveName: String): GameReplay =
    val path = fwc.savesDirectory + "/" + userId + "/" + saveName
    if !Files.exists(Paths.get(path)) then
      throw new FWCException("The save file does not exist")
    else
      GameReplay.fromJson(ujson.read(Files.readString(Paths.get(path))))
}
