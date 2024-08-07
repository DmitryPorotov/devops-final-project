package generator.languageAgnosticTypeObjects

case class Arr(
                valueType: Option[TypeObject] = None,
                override val isOptional: Boolean = false,
              ) extends TypeObject(isOptional)