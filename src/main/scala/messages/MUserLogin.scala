package messages

import game.Player
import network.McPacket

case class MUserLogin(name: String) extends Message
case class MUserLogout(name: String) extends Message
case class MNetworkMessageOut(player: Player, msg: McPacket) extends Message
case class MNetworkIn(player: Player, msg: McPacket) extends Message