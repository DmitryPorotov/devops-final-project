package generator

import fwc.JsonSerializable
import generator.languageAgnosticTypeObjects.TypeObject
import languageAgnosticTypeObjects.*

abstract class GeneratorTemplate {
  private val _builder: StringBuilder = StringBuilder()

  protected var header: String = ""

  override def toString: String =
    addFileHeader() + _builder.toString()
    
  def toString(unionHeader: Boolean): String =
    addFileHeader(unionHeader) + _builder.toString()

  def generateUnion(union: String): Unit = {
    _builder.underlying.append(
      if !union.isBlank then DependenciesMap.buildUnionType(
      this,
      union,
      if union == "Action"
      then InstanciesCollection.actions.foldLeft[Seq[String]](Seq())((acc, v) => {
        val (cat, map) = v
        acc :++ map.keys.toSeq
      })
      else if union == "SubPhase" then
        InstanciesCollection.phases.keys.toSeq
      else throw new RuntimeException("Unknown union" + union)
      )
      else ""
    )
  }

  def generate(instancies: Map[String, JsonSerializable]): Unit = {
    val dependencies = instancies.foldLeft(Set[String]())((acc, tup) => {
      val (name, v) = tup
      acc ++ buildTypes(name, v)
    })

    header = DependenciesMap.getHeaderLines(this, dependencies)
  }

  private def buildTypes(key: String, value: JsonSerializable): Set[String] = {
    def objTypeMatch(acc: Set[String], obj: TypeObject): Set[String] = {
      obj match
        case Obj(n, k, v, _) =>
          if n.isDefined then acc + n.head
          else {
            if k.nonEmpty then
              objTypeMatch(acc, k.head); objTypeMatch(acc, v.head)
            else acc
          }
        case Arr(v, _) =>
          if v.isDefined && (v.head.isInstanceOf[Obj] || v.head.isInstanceOf[Enum]) then objTypeMatch(acc, v.head)
          else acc
        case Enum(v, _) => acc + v
        case _ => acc
    }

    _builder.underlying.append(classHeader(key))
    val dependencies = value.toJson.obj.foldLeft(Set[String]())((acc, cur) => {
      val (k,v) = cur
      val (typeStr, typeObj) = probeType(value, k, v)
      var typesToImport = _builder.underlying.append(getField(k, typeStr, typeObj.isOptional))
      objTypeMatch(acc, typeObj)
    })

    _builder.underlying.append(classFooter)

    dependencies
  }

  protected def addFileHeader(isUnionHeader: Boolean = false): String
  
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

  protected def probeType(fromObj: JsonSerializable, objKey: String,  value: ujson.Value): (String, TypeObject) = {
    def addOptional(t: TypeObject): String = {
      s"${if optionalTypeIsOnKey then "" else optionalType + genericOpenBracket}${probeTypeInner(
        if t.isInstanceOf[Str] then
          t.asInstanceOf[Str].copy(isOptional = false)
        else if t.isInstanceOf[Int] then
          t.asInstanceOf[Int].copy(isOptional = false)
        else if t.isInstanceOf[Bool] then
            t.asInstanceOf[Bool].copy(isOptional = false)
        else if t.isInstanceOf[Enum] then
          t.asInstanceOf[Enum].copy(isOptional = false)
        else throw new RuntimeException(s"Type $t is should not be optional.")
      )._1}${if optionalTypeIsOnKey then "" else genericCloseBracket}"
    }

    def probeTypeInner(t: TypeObject): (String, TypeObject) =
      val typeStr = t match
        case Str(None, o) =>
          if o then addOptional(t)
          else strType
        case Str(v, _) => s"$strType $strValuePrefix\"${v.mkString("")}\""
        case Int(o) =>
          if o then addOptional(t)
          else intType
        case Bool(o) =>
          if o then addOptional(t)
          else boolType
        case Obj(None, None, None, _) => objType
        case Obj(None, k, v, _) => s"$objType$genericOpenBracket${probeTypeInner(k.head)._1}, ${probeTypeInner(v.head)._1}$genericCloseBracket"
        case Obj(n, None, None, _) => n.mkString("")
        case Enum(n, _) =>
          if enumIsString then strType
          else n
        case Arr(None, _) => arrType
        case Arr(n, o) =>
          if arrTypeIsPostfix then s"${probeTypeInner(n.head)._1}$arrType"
          else s"${if o && !optionalTypeIsOnKey then optionalType + genericOpenBracket else ""}" +
           s"$arrType$genericOpenBracket${probeTypeInner(n.head)._1}" +
           s"$genericCloseBracket${if o && !optionalTypeIsOnKey then genericCloseBracket else ""}"

      (typeStr, t)
    probeTypeInner(LanguageAgnosticTypeProbes.probeType(fromObj, objKey, value))
  }


}
