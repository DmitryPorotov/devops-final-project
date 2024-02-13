package fwc.communication.reactions

import java.nio.file.{Files, Paths}
import scala.jdk.CollectionConverters._

object ReactionListSavedGames {
  def apply(userId: Int): Seq[String] =
    val path = Paths.get(fwc.savesDirectory + "/" + userId)
    if !Files.exists(path) then
      Seq()
    else
      Files.list(path).iterator().asScala.filter(Files.isRegularFile(_)).map(_.getFileName.toString).toSeq
}
