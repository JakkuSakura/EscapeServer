import io.netty.buffer.ByteBuf

object Utils {
    def getHexAndChar(buf: ByteBuf): String = {
        val sb = new StringBuilder(buf.readableBytes() * 3 * 2 + 2)
        try {
            buf.forEachByte(x => {
                sb.append(String.format("%02x ", x))
                true
            })
            sb.append("\n")

            buf.forEachByte(x => {
                if (x.toChar.isLetterOrDigit) {
                    sb.append(String.format("%c  ", x + 128))
                } else {
                    sb.append("** ")
                }
                true
            })
            sb.append("\n")
            sb.toString()
        } catch {
            case ex: Exception =>
                ex.printStackTrace()
                throw ex
        }

    }
}
