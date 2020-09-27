import java.util

import io.netty.buffer.{ByteBuf, Unpooled}
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder

class PacketSplitter() extends ByteToMessageDecoder {
    override def decode(ctx: ChannelHandlerContext, in: ByteBuf, out: util.List[AnyRef]): Unit = {
        val buf = new PacketByteBuf(in)
        val reader = buf.readerIndex()
        try {
            val length = buf.readVarInt()
            if (buf.readableBytes() >= length) {
                val start_index = buf.readerIndex()
                val packet_id = buf.readVarInt()
                val read_length = buf.readerIndex() - start_index
                val data = buf.readBytes(length - read_length)
                if (data.readableBytes() > 0)
                    out.add(new RawPacket(packet_id, data))
            }

        } catch {
            case ex: Exception =>
                buf.readerIndex(reader)
        }

    }
}
