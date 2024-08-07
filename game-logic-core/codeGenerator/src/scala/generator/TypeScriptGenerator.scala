package generator

class TypeScriptGenerator extends GeneratorTemplate {

  override protected def addFileHeader(isUnionHeader: Boolean = false): String = ""

  override protected def classHeader(className: String): String = s"\nexport interface $className {\n"

  override protected val classFooter: String = "}\n"

  override protected def getField(key: String, _type: String, isOptional: Boolean): String = s"  $key${if isOptional then "?" else ""}: $_type;\n"

  override protected val strType: String = "string"
  override protected val strValuePrefix: String = "= "
  override protected val intType: String = "number"
  override protected val boolType: String = "boolean"
  override protected val objType: String = "Object"
  override protected val arrType: String = "Array"
  override protected val optionalType: String = ""
  override protected val genericOpenBracket: String = "<"
  override protected val genericCloseBracket: String = ">"
  override protected val optionalTypeIsOnKey: Boolean = true
}
