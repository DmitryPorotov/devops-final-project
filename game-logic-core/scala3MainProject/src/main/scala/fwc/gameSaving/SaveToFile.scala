package fwc.gameSaving
import ujson.Value
import java.nio.file.{Paths, Files}
import java.nio.charset.StandardCharsets

class SaveToFile extends Saver {
  override def save(name: String, json: Value): Unit = {
    Files.write(Paths.get(s"saves/$name.json"), json.render(fwc.jsonIndentation).getBytes(StandardCharsets.UTF_8))
  }
}
