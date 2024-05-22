package fwc.communication.reactions

import fwc.gameSaving.GameReplay

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}
import java.text.SimpleDateFormat
import java.util.Date

object ReactionSaveGame {
  def apply(userId: Int, gameId: String, saveName: String, gameReplay: GameReplay): String =
    val fileName: String = userId + "/"
      + gameId + "--" + saveName + "--"
      + new SimpleDateFormat("yyyy-MM-dd'T'hh-mm-ss").format(new Date())
      + ".json"
    if !Files.exists(Paths.get(fwc.savesDirectory + "/" + userId)) then
      Files.createDirectory(Paths.get(fwc.savesDirectory + "/" + userId))
    Files.write(Paths.get(fwc.savesDirectory + "/" + fileName), gameReplay.toJsonString.getBytes(StandardCharsets.UTF_8))
    fileName
}
