package generator

import fwc.JsonSerializable

abstract class GeneratorTemplate {
  protected val _builder: StringBuilder = StringBuilder()

  def builder: StringBuilder = _builder

  def generate(instancies: Map[String, JsonSerializable]): Unit = {
    addFileHeader()
    instancies.foreach((name, v) => {
      buildTypes(name, v)
    })
  }

  private def buildTypes(key: String, value: JsonSerializable): Unit = {
    _builder.underlying.append(classHeader(key))
    value.toJson.obj.foreach((k, v) => {
      val (type_, isOptional) = probeType(value, k, v)
      _builder.underlying.append(getField(k, type_, isOptional))
    })
    _builder.underlying.append(classFooter)
  }

  protected def addFileHeader(): Unit
  
  protected def classHeader(className: String): String
  
  protected val classFooter: String = ""
  
  protected def getField(key: String, _type: String, isOptional: Boolean): String

  protected val strType: String

  protected val strValuePrefix: String

  protected val intType: String

  protected val boolType: String

  protected val objType: String

  protected val arrType: String

  protected val arrTypeIsPostfix: Boolean = false

  protected val optionalType: String

  protected val optionalTypeIsOnKey: Boolean = false

  protected val enumIsString: Boolean = false

  protected val genericOpenBracket: String

  protected val genericCloseBracket: String

  protected def probeType(fromObj: JsonSerializable, objKey: String,  value: ujson.Value): (String, Boolean) = {
    def probeTypeInner(str: String): (String, Boolean) =
      var isOptional = false
      val typeStr = str match
        case "str" => strType
        case "int" => intType
        case "bool" => boolType
        case "obj" => objType
        case s =>
          if s.startsWith("arr") then
            val parts = s.split("<", 2)
            if parts(1).startsWith("any") then
              arrType
            else
              if arrTypeIsPostfix then s"${probeTypeInner(parts(1).dropRight(1))._1}$arrType"
              else s"$arrType$genericOpenBracket${probeTypeInner(parts(1).dropRight(1))._1}$genericCloseBracket"
          else if s.startsWith("obj") then
            val inner = s.split("<", 2)(1).dropRight(1)
            val parts = inner.split(",")
            s"$objType$genericOpenBracket${probeTypeInner(parts(0))._1}, ${probeTypeInner(parts(1))._1}$genericCloseBracket"
          else if s.startsWith("enum") then
            if enumIsString then strType
            else s.split("<", 2)(1).dropRight(1)
          else if s.contains("-opt") then
            isOptional = true
            s"${if optionalTypeIsOnKey then "" else optionalType + genericOpenBracket}${probeTypeInner(s.split("-")(0))._1}${if optionalTypeIsOnKey then "" else genericCloseBracket}"
          else if s.startsWith("str-val-") then
            s"$strType $strValuePrefix\"${s.split("str-val-")(1)}\""
          else s
      (typeStr, isOptional)
    probeTypeInner(LanguageAgnosticTypeProbes.probeType(fromObj, objKey, value))
  }
}
