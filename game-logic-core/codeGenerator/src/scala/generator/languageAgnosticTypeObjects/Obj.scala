package generator.languageAgnosticTypeObjects

case class Obj(
                name: Option[String] = None,
                keyType: Option[TypeObject] = None,
                valueType: Option[TypeObject] = None,
                override val isOptional: Boolean = false,
              ) extends TypeObject(isOptional)