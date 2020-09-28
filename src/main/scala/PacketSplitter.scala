import java.util

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder

class PacketSplitter() extends ByteToMessageDecoder {
    override def decode(ctx: ChannelHandlerContext, in: ByteBuf, out: util.List[AnyRef]): Unit = {
        val buf = new PacketByteBuf(in)
        val reader = buf.buf.readerIndex()
        try {
            val length = buf.readVarInt()
            if (buf.readableBytes >= length) {
                val start_index = buf.buf.readerIndex()
                val packet_id = buf.readByte()
                assert(packet_id <= 0x7F)
                val read_length = buf.buf.readerIndex - start_index
                val data = buf.readBytes(length - read_length)
                val packet = new McPacket(packet_id, data)
                out.add(packet)
                println("Received a packet")
            }

        } catch {
            case ex: Exception =>
                buf.buf.readerIndex(reader)
        }

    }
}
