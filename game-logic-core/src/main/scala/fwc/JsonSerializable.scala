package fwc

abstract trait JsonSerializable {
  def toJson: ujson.Value

  def toJsonString: String = toJson.render(fwc.jsonIndentation)

//  override def toString: String = toJsonString
}
