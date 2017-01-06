package exceptions

case class NoResourceFoundException(message : String) extends Exception

case class ModelValidationException(message : String) extends Exception
