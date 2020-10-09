package game

import chunk.ChunkPos
import com.github.steveice10.mc.protocol.data.game.entity.metadata.Position
import com.github.steveice10.mc.protocol.data.game.entity.player.GameMode
import com.github.steveice10.mc.protocol.data.game.setting.Difficulty
import com.github.steveice10.mc.protocol.data.game.world.WorldType
import com.github.steveice10.mc.protocol.data.game.{PlayerListEntry, PlayerListEntryAction}
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientChatPacket
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPositionRotationPacket
import com.github.steveice10.mc.protocol.packet.ingame.server._
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.{ServerPlayerAbilitiesPacket, ServerPlayerChangeHeldItemPacket, ServerPlayerPositionRotationPacket}
import com.github.steveice10.mc.protocol.packet.ingame.server.world.{ServerSpawnPositionPacket, ServerUpdateTimePacket}
import messages._
import player.Player

import scala.collection.concurrent

object Server extends Thread {
    // First 8 bytes of the SHA-1 hash of the world's seed.
    var hashed_seed: Long = -164964450544091349L

    val players = new concurrent.TrieMap[String, Player]
    val message_queue = new MessageQueue()
    val world: World = new CubedWorld()
    var teleport_id: Int = 0
    var command_handler: CommandHandler = new CommandHandler()

    def addPlayer(player: Player): Unit = {
        players(player.player_name) = player
        player.y = 64

        message_queue.broadcast(MUserLogin(player.player_name))

        message_queue.broadcast(MNetworkOut(player, new ServerChatPacket("Hi %s, you have logged in".format(player.player_name))))
        message_queue.broadcast(MNetworkOut(player, new ServerJoinGamePacket(0, false, GameMode.CREATIVE, 0, hashed_seed, 100, WorldType.DEFAULT, 16, false, true)))
        // plugin packet
        message_queue.broadcast(MNetworkOut(player, new ServerDifficultyPacket(Difficulty.PEACEFUL, true)))
        message_queue.broadcast(MNetworkOut(player, new ServerPlayerAbilitiesPacket(true, true, true, false, 0.05f, 0.1f)))
        message_queue.broadcast(MNetworkOut(player, new ServerPlayerChangeHeldItemPacket(0)))
        message_queue.broadcast(MNetworkOut(player, new ServerDeclareRecipesPacket(Array.empty)))
        sendPlayerPosition(player)

        message_queue.broadcast(MNetworkOut(player, new ServerPlayerListEntryPacket(PlayerListEntryAction.ADD_PLAYER, Array(new PlayerListEntry(player.profile, GameMode.CREATIVE)))))
        //ServerEntityMetadataPacket(entityId=0, metadata=[EntityMetadata(id=16, type=BYTE, value=127)])

        message_queue.broadcast(MNetworkOut(player, new ServerSpawnPositionPacket(new Position(player.x.toInt, player.y.toInt, player.z.toInt))))

        message_queue.broadcast(MNetworkOut(player, new ServerUpdateTimePacket(0, 0)))

        sendNearbyChunks(player, first_load = true)
        sendPlayerPosition(player)
    }

    def sendNearbyChunks(player: Player, first_load: Boolean): Unit = {
        val chk = world.toChunkPos(player.x.toInt, player.y.toInt)
        for (x <- chk.x - 3 until chk.x + 3)
            if (world.isInBoundary(ChunkPos(x, chk.z)))
                for (z <- chk.z - 3 until chk.z + 3) {
                    if (world.isInBoundary(ChunkPos(x, z)))
                        message_queue.broadcast(MNetworkOut(player, world.getChunk(ChunkPos(x, z), first_load)))
                }

    }

    def sendPlayerPosition(player: Player): Unit = {
        message_queue.broadcast(MNetworkOut(player, new ServerPlayerPositionRotationPacket(player.x, player.y, player.z, player.yaw, player.pitch, teleport_id)))
        teleport_id += 1
    }


    def removePlayer(id: String): Unit = {
        if (players.remove(id).isDefined)
            message_queue.broadcast(MUserLogout(id))
    }

    def playerUpdate(player: Player): Unit = {
        if (player.hp > 0) {
            //player.z += 0.1f
        }
    }

    def processNetworkIn[T](clazz: Class[T], handle: (Player, T) => Unit): Unit = {
        message_queue.processAll(clazz, in => {
            val MNetworkIn(player, msg) = in
            val packet = msg.asInstanceOf[T]
            handle(player, packet)
        })
    }

    def updateServer(): Unit = {
        processNetworkIn[ClientPlayerPositionRotationPacket](classOf[ClientPlayerPositionRotationPacket], (player, packet) => {
            player.x = packet.getX.asInstanceOf[Float]
            player.y = packet.getY.asInstanceOf[Float]
            player.z = packet.getZ.asInstanceOf[Float]
            player.pitch = packet.getPitch
            player.yaw = packet.getYaw
        })

        processNetworkIn[ClientChatPacket](classOf[ClientChatPacket], (player, packet) => {
            command_handler.process(player, packet.getMessage)
        })

        players.foreachEntry((_, p) => playerUpdate(p))

        Thread.sleep(5)
    }


    override def run(): Unit = {
        while (true) {
            updateServer()
        }
    }


}
