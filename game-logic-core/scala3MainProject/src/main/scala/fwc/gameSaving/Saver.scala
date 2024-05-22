package fwc.gameSaving

trait Saver {
  def save(name: String, json: ujson.Value): Unit
}
