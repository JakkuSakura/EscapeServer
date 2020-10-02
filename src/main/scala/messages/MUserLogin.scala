package messages

case class MUserLogin(name: String) extends Message
case class MUserLogout(name: String) extends Message

