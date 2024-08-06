package generator

class LuaGenerator extends GeneratorTemplate {

  override protected def addFileHeader(): String = ""

  override protected def classHeader(className: String): String = s"\n---@class $className : userdata\n"

  override protected def getField(key: String, _type: String, isOptional: Boolean): String = s"---@field $key $_type\n"

  override protected val strType: String = "string"
  override protected val strValuePrefix: String = ""
  override protected val intType: String = "number"
  override protected val boolType: String = "boolean"
  override protected val objType: String = "table"
  override protected val arrType: String = "[]"
  override protected val optionalType: String = ""
  override protected val genericOpenBracket: String = "<"
  override protected val genericCloseBracket: String = ">"
  override protected val enumIsString: Boolean = true
  override protected val optionalTypeIsOnKey: Boolean = true
  override protected val arrTypeIsPostfix: Boolean = true
}
