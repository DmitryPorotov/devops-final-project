package generator.languageAgnosticTypeObjects

case class Bool(
                 override val isOptional: Boolean = false
               ) extends TypeObject(isOptional)
