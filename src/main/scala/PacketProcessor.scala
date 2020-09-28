import java.util.UUID

import io.netty.buffer.Unpooled
import io.netty.channel.{ChannelHandlerContext, SimpleChannelInboundHandler};

class PacketProcessor extends SimpleChannelInboundHandler[McPacket] {
    var client: Client = _

    override def channelActive(ctx: ChannelHandlerContext): Unit = {
        println("Connection established " + ctx)
        client = new Client(ctx, null)
    }

    override def channelRead0(ctx: ChannelHandlerContext, packet: McPacket): Unit = {
        println("ChannelRead invoked:" + packet)

        client.connection_state match {
            // 1. C→S: Handshake with Next State set to 2 (login) or 1 (status)
            case Client.HANDSHAKE => if (packet.getPacketId == 0x00) {
                val hs = new HandShaking(packet.getData)
                println("Next state: " + hs.next_state + ", protocol version: " + hs.protocol_version)
                client.connection_state = hs.next_state
            }
            case Client.STATUS => packet.getPacketId match {
                // Request, Empty packet
                case 0x00 =>
                    val response = new McPacket(0x00, Unpooled.buffer())
                    val json = "{\"version\":" +
                      "{\"name\":\"1.15.2\",\"protocol\":578}," +
                      "\"players\":{\"max\":100,\"online\":0}," +
                      "\"description\":{\"text\":\"Escape Server\"}}"
                    // TODO add image here

                    response.writeString(json)

                    ctx.channel().writeAndFlush(response)
                // Ping
                case 0x01 =>
                    // Pong
                    ctx.channel().writeAndFlush(packet)
                    ctx.close()
            }
            case Client.LOGIN =>
                packet.getPacketId match {
                    case 0x00 =>
                        // 2. C→S: Login Start
                        val login_start = new LoginStart(packet.getData)
                        println("Player name: " + login_start.player_name)
                        client.player = new Player()

                        client.player.player_name = login_start.player_name

                        if (Configuration.ENABLE_ENCRYPTION) {
                            // 3. S→C: Encryption Request
                            ctx.channel().writeAndFlush(new EncryptionRequest())
                            println("Sent encryption request")
                        } else {
                            ctx.channel().writeAndFlush(new LoginSuccess(UUID.randomUUID(), client.player.player_name))
                            client.connection_state = Client.PLAY
                            EscapeServer.add_client(client)
                        }


                    case 0x01 =>
                        // 4. Client auth
                        // 5. C→S: Encryption Response
                        val en_res = new EncryptionResponse(packet.getData)
                        println(en_res)

                        // 6. Server auth, both enable encryption
                        // TODO verify token from https://sessionserver.mojang.com/session/minecraft/join

                        // 7. S→C: Set Compression (optional)
                        // no compression if no sending
                        // ctx.channel().writeAndFlush(new SetCompression(-1))

                        // 8. S→C: Login Success
                        ctx.channel().writeAndFlush(new LoginSuccess(UUID.randomUUID(), client.player.player_name))
                        client.connection_state = Client.PLAY
                        EscapeServer.add_client(client)
                    case _ => println("Unknown")
                }
            case Client.PLAY =>
            // TODO
            case _ => println("Unknown")
        }
    }

    override def channelInactive(ctx: ChannelHandlerContext): Unit = {
        println("Inactive " + ctx)
        if (client.player != null)
            EscapeServer.removeClient(client.player.player_name)
        client = null
    }
}