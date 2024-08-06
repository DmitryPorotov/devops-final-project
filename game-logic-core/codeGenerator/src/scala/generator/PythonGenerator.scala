package generator


class PythonGenerator extends GeneratorTemplate {

  override protected def addFileHeader(): Unit = {
    _builder.underlying.append("from typing import TypedDict, Optional\n")
    _builder.underlying.append("from server_module.game_state.military_unit import HouseType, MilitaryUnitType, MilitaryUnit\n")
    _builder.underlying.append("from server_module.game_state.track_type import TrackType\n")
    _builder.underlying.append("from server_module.game_state.order import Order, OrderType\n")
  }

  override protected def getField(key: String, _type: String, isOptional: Boolean): String = {
    "    " + key + (if _type.isBlank then "" else ": " + _type) + "\n"
  }

  override protected def classHeader(className: String): String = "\n\nclass " + className + "(TypedDict):\n"

  protected val arrType: String = "list"

  protected val strType: String = "str"

  protected val strValuePrefix: String = " # = "

  protected val intType: String = "int"

  protected val boolType: String = "bool"

  protected val objType: String = "dict"

  protected val optionalType: String = "Optional"

  protected val genericOpenBracket: String = "["

  protected val genericCloseBracket: String = "]"

}
