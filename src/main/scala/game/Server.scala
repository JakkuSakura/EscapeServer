package game

import java.util.concurrent.LinkedBlockingQueue

import com.github.steveice10.mc.protocol.data.game.chunk.{Chunk, Column}
import com.github.steveice10.mc.protocol.data.game.entity.metadata.Position
import com.github.steveice10.mc.protocol.data.game.entity.player.GameMode
import com.github.steveice10.mc.protocol.data.game.world.WorldType
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPositionRotationPacket
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerJoinGamePacket
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.{ServerPlayerChangeHeldItemPacket, ServerPlayerPositionRotationPacket}
import com.github.steveice10.mc.protocol.packet.ingame.server.world.{ServerChunkDataPacket, ServerSpawnPositionPacket}
import com.github.steveice10.opennbt.tag.builtin.{CompoundTag, LongArrayTag}
import messages._
import player.Player
import utils.BitArray

import scala.collection.concurrent

object Server extends Thread {
    // First 8 bytes of the SHA-1 hash of the world's seed.
    var hashed_seed: Long = 0

    val players = new concurrent.TrieMap[String, Player]
    val message_queue = new MessageQueue()
    val world: World = new CubedWorld()
    var teleport_id: Int = 0

    def addPlayer(player: Player): Unit = {
        players(player.player_name) = player
        message_queue.broadcast(MUserLogin(player.player_name))
        for (x <- -3 until 4)
            for (z <- -3 until 4)
                message_queue.broadcast(MNetworkOut(player, sendChunk(x, z)))
        player.y = 64

        message_queue.broadcast(MNetworkOut(player, new ServerJoinGamePacket(0, false, GameMode.SURVIVAL, 0, 0, 100, WorldType.DEFAULT, 16, false, true)))
        message_queue.broadcast(MNetworkOut(player, new ServerSpawnPositionPacket(new Position(0, 64, 0))))
        message_queue.broadcast(MNetworkOut(player, new ServerPlayerPositionRotationPacket(player.x, player.y, player.z, player.yaw, player.pitch, teleport_id)))
        message_queue.broadcast(MNetworkOut(player, new ServerPlayerChangeHeldItemPacket(0)))
    }

    def sendChunk(x: Int, z: Int): ServerChunkDataPacket = {
        val chunks = new Array[Chunk](16)
        for (i <- 0 until 16) {
            chunks(i) = new Chunk()
        }
        val hm = new CompoundTag("HeightMap")
        val ba = new BitArray(9, 256)
        hm.put(new LongArrayTag("MOTION_BLOCKING", ba.longArray))
        val col = new Column(x, z, chunks, Array.empty[CompoundTag], hm)
        val cd = new ServerChunkDataPacket(col)
        cd
    }

    def removePlayer(id: String): Unit = {
        if (players.remove(id).isDefined)
            message_queue.broadcast(MUserLogout(id))
    }

    def playerUpdate(player: Player): Unit = {
        if (player.hp > 0) {
            //player.z += 0.1f
            //teleport_id += 1
        }
    }

    def updateServer(): Unit = {
        val clientPlayerPositionRotationQueue = message_queue.getQueue(classOf[ClientPlayerPositionRotationPacket])
          .asInstanceOf[LinkedBlockingQueue[MNetworkIn]]
        while (!clientPlayerPositionRotationQueue.isEmpty) {
            val MNetworkIn(player, msg) = clientPlayerPositionRotationQueue.poll()
            val packet = msg.asInstanceOf[ClientPlayerPositionRotationPacket]
            player.x = packet.getX.asInstanceOf[Float]
            player.y = packet.getY.asInstanceOf[Float]
            player.z = packet.getZ.asInstanceOf[Float]
            player.pitch = packet.getPitch
            player.yaw = packet.getYaw
        }


        players.foreachEntry((_, p) => playerUpdate(p))

        Thread.sleep(5)
    }


    override def run(): Unit = {
        while (true) {
            updateServer()
        }
    }


}
