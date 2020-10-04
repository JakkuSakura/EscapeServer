package game

import java.util.concurrent.LinkedBlockingQueue

import com.github.steveice10.mc.protocol.data.game.chunk.Chunk
import messages.{MNetworkIn, MNetworkMessageOut, MUserLogin, MUserLogout, MessageQueue}
import network.Client
import player.Player
import protocols.JoinGame

import scala.collection.{concurrent, mutable}

object Server extends Thread {
    // First 8 bytes of the SHA-1 hash of the world's seed.
    var hashed_seed: Long = 0

    val players = new concurrent.TrieMap[String, Player]
    val message_queue = new MessageQueue()
    val packets = new LinkedBlockingQueue[MNetworkIn]()
    val world: World = new CubedWorld()

    def addPlayer(player: Player): Unit = {
        players(player.player_name) = player
        message_queue.broadcast(MUserLogin(player.player_name))
        message_queue.broadcast(MNetworkMessageOut(player, new JoinGame(player)))
        //message_queue.broadcast(MNetworkMessageOut(player, sendChunk(0, 0)))
    }

    def sendChunk(x: Int, z: Int): Chunk = {
        null
    }
    def removePlayer(id: String): Unit = {
        if (players.remove(id).isDefined)
            message_queue.broadcast(MUserLogout(id))
    }

    def playerUpdate(player: Player): Unit = {
        if (player.hp > 0) {

        }
    }

    def updateServer(): Unit = {
        message_queue.getQueue(classOf[MNetworkIn]).drainTo(packets)
        players.foreachEntry((_, p) => playerUpdate(p))

        Thread.sleep(5)
    }


    override def run(): Unit = {
        while (true) {
            updateServer()
        }
    }


}
