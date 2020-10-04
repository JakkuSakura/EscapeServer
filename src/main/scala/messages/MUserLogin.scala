package messages

import com.github.steveice10.packetlib.packet.Packet
import player.Player

case class MUserLogin(name: String) extends Message
case class MUserLogout(name: String) extends Message
case class MNetworkOut(player: Player, msg: Packet) extends Message
case class MNetworkIn(player: Player, msg: Packet) extends Message