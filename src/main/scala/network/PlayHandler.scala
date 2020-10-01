package network

class PlayHandler extends PacketHandler {
    override def apply(packet: McPacket): Unit = {
        println("Play")
    }
}
