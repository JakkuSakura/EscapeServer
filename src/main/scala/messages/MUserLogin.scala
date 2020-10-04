package messages

import player.Player
import protocols.McPacket

case class MUserLogin(name: String) extends Message
case class MUserLogout(name: String) extends Message
case class MNetworkMessageOut(player: Player, msg: McPacket) extends Message
case class MNetworkIn(player: Player, msg: McPacket) extends Message