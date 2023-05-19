package fwc

package object gameSaving {
  def _saveGame(saver: Saver)(name: String, json: ujson.Value): Unit = {
    saver.save(name, json)
  }

  val saveGame: (String, ujson.Value) => Unit = _saveGame(new SaveToFile)
}
