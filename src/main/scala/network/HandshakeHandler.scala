package network

import java.util.UUID

import game.{Server, Player}
import io.netty.buffer.Unpooled
import utils.Configuration


class HandshakeHandler(ch: InClientHandler, client: Client) extends PacketHandler{
    def login(): Unit = {
        client.ctx.channel().writeAndFlush(new LoginSuccess(UUID.randomUUID(), client.player.player_name, client.protocol_version))
        client.connection_state = ConnectionState.PLAY
        ch.current_handler = new PlayHandler(client)
    }

    def apply(packet: McPacket): Unit = {
        val ctx = client.ctx

        client.connection_state match {
            // 1. C→S: Handshake with Next State set to 2 (login) or 1 (status)
            case ConnectionState.HANDSHAKE => if (packet.getPacketId == 0x00) {
                val hs = new HandShaking(packet.getData)
                println("Next state: " + hs.next_state + ", protocol version: " + hs.protocol_version)
                client.connection_state = ConnectionState(hs.next_state)
                client.protocol_version = hs.protocol_version
            }
            case ConnectionState.STATUS => packet.getPacketId match {
                // Request, Empty packet
                case 0x00 =>
                    val response = new McPacket(0x00, Unpooled.buffer())
                    val json = "{\"version\":" +
                      "{\"name\":\"1.15.2\",\"protocol\":578}," +
                      "\"players\":{\"max\":100,\"online\":0}," +
                      "\"description\":{\"text\":\"Escape Server\"}}"
                    // TODO add image here

                    response.writeVarString(json)
                    ctx.channel().writeAndFlush(response)
                // Ping
                case 0x01 =>
                    // network.Pong
                    ctx.channel().writeAndFlush(packet)
                    ctx.close()
            }
            case ConnectionState.LOGIN =>
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
                            login()
                        }


                    case 0x01 =>
                        // 4. network.Client auth
                        // 5. C→S: Encryption Response
                        val en_res = new EncryptionResponse(packet.getData)
                        println(en_res)

                        // 6. Server auth, both enable encryption
                        // TODO verify token from https://sessionserver.mojang.com/session/minecraft/join

                        // 7. S→C: Set Compression (optional)
                        // no compression if no sending
                        // ctx.channel().writeAndFlush(new network.SetCompression(-1))

                        // 8. S→C: Login Success
                        login()

                    case _ => println("Unknown")
                }
            case _ =>
                println("Unknown")
        }
    }
}
