package game

import java.util.concurrent.LinkedBlockingQueue

import com.github.steveice10.mc.protocol.data.game.{PlayerListEntry, PlayerListEntryAction}
import com.github.steveice10.mc.protocol.data.game.chunk.{Chunk, Column, FlexibleStorage}
import com.github.steveice10.mc.protocol.data.game.entity.player.GameMode
import com.github.steveice10.mc.protocol.data.game.setting.Difficulty
import com.github.steveice10.mc.protocol.data.game.world.WorldType
import com.github.steveice10.mc.protocol.packet.ingame.client.player.{ClientPlayerPositionRotationPacket, ClientPlayerSwingArmPacket}
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.{ServerPlayerAbilitiesPacket, ServerPlayerChangeHeldItemPacket, ServerPlayerPositionRotationPacket}
import com.github.steveice10.mc.protocol.packet.ingame.server.world.{ServerChunkDataPacket, ServerUpdateTimePacket}
import com.github.steveice10.mc.protocol.packet.ingame.server.{ServerChatPacket, ServerDeclareRecipesPacket, ServerDifficultyPacket, ServerJoinGamePacket, ServerPlayerListEntryPacket}
import com.github.steveice10.opennbt.tag.builtin.{CompoundTag, LongArrayTag}
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

    def addPlayer(player: Player): Unit = {
        players(player.player_name) = player
        player.y = 10

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

        //message_queue.broadcast(MNetworkOut(player, new ServerSpawnPositionPacket(new Position(player.x.toInt, player.y.toInt, player.z.toInt))))
        //message_queue.broadcast(MNetworkOut(player, new ServerPlayerListDataPacket()))
        message_queue.broadcast(MNetworkOut(player, new ServerUpdateTimePacket(0,0)))


        for (x <- -3 until 3)
            for (z <- -3 until 3)
                message_queue.broadcast(MNetworkOut(player, sendChunk(x, z)))

        sendPlayerPosition(player)
    }
    def sendPlayerPosition(player: Player): Unit = {
        message_queue.broadcast(MNetworkOut(player, new ServerPlayerPositionRotationPacket(player.x, player.y, player.z, player.yaw, player.pitch, teleport_id)))
        teleport_id += 1
    }

    def sendChunk(x: Int, z: Int): ServerChunkDataPacket = {
        val chunks = new Array[Chunk](16)
        for (i <- 0 until 16) {
            //chunks(i) = new Chunk()
        }
        val ba = new FlexibleStorage(9, 256)
        for (i <- 0 until 256)
            ba.set(i, 0)

        val hm = new CompoundTag("")
        hm.put(new LongArrayTag("MOTION_BLOCKING", ba.getData))
        val col = new Column(x, z, chunks, Array.empty[CompoundTag], hm, new Array[Int](1024))
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
        val client_swing_arms = message_queue.getQueue(classOf[ClientPlayerSwingArmPacket])
          .asInstanceOf[LinkedBlockingQueue[MNetworkIn]]
        while (!client_swing_arms.isEmpty) {
            val MNetworkIn(player, msg) = client_swing_arms.poll()
            val packet = msg.asInstanceOf[ClientPlayerSwingArmPacket]
            //message_queue.broadcast(MNetworkOut(player, new ServerEntityAnimationPacket(0, Animation.SWING_ARM)))
            packet.getHand
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
