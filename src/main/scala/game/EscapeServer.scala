package game

import messages.{MUserLogin, MessageQueue}
import network.Client

import scala.collection.concurrent

object EscapeServer extends Runnable {
    // First 8 bytes of the SHA-1 hash of the world's seed.
    var hashed_seed: Long = 0

    val clients = new concurrent.TrieMap[String, Player]
    var theThread: Thread = _
    val message_queue = new MessageQueue()
    val world: World = new CubedWorld()
    def add_client(player: Player): Unit = {
        clients(player.player_name) = player
        message_queue.broadcast(MUserLogin(player.player_name))
    }

    def removeClient(id: String): Unit = clients.remove(id)

    def playerUpdate(player: Player): Unit = {
    }

    def customPreloadStep(): Unit = {}

    def updateServer(): Unit = {

    }


    override def run(): Unit = {
        while (true) {
            updateServer()
        }
    }

    def newThread(): Unit = {
        new Thread(EscapeServer).start()
    }


}
