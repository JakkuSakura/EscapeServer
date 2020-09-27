import io.netty.buffer.{ByteBuf, Unpooled}
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder

class PacketEncoder extends MessageToByteEncoder[McPacket] {
    override def encode(ctx: ChannelHandlerContext, msg: McPacket, out: ByteBuf): Unit = {
        val packet_id_buf = new PacketByteBuf(Unpooled.buffer())
        packet_id_buf.writeVarInt(msg.getPacketId)

        val buf = new PacketByteBuf(out)
        buf.writeVarInt(packet_id_buf.readableBytes + msg.readableBytes)
        buf.writeBytes(packet_id_buf.buf)
        buf.writeBytes(msg.buf)

    }
}
