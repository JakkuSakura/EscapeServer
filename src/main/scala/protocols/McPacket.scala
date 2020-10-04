package protocols

import java.util.UUID

import game.Server
import io.netty.buffer.{ByteBuf, Unpooled}
import player.Player
import utils.{Crypto, Utils}

class McPacket(private var packet_id: Byte, buf_ : ByteBuf) extends PacketByteBuf(buf_) {

    def this() = this(0, Unpooled.buffer())

    def this(buf: ByteBuf) = this(0, buf)

    def this(packet_id: Byte) = this(packet_id, Unpooled.buffer())

    def this(packet_id: Int) = this(packet_id.toByte, Unpooled.buffer())

    // It seems like there is no packet id greater 0x7F yet
    final def getPacketId: Byte = packet_id

    final def getData: ByteBuf = buf

    final def setPacketId(id: Byte): Unit = packet_id = id

    final def setData(data: ByteBuf): Unit = buf = data


    override def toString: String = {
        String.format("Packet id: %02x\nData: %d\n", packet_id, readableBytes) + Utils.getHexAndChar(buf)
    }
}

