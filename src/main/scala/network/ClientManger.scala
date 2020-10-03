package network

import java.util.concurrent.ConcurrentHashMap

import game.Player

object ClientManger {

    val clients = new ConcurrentHashMap[String, Client]()
    def addClient(client: Client): Unit = {
        clients.put(client.player.player_name, client)
    }
    def removeClient(client: Client): Unit = {
        clients.remove(client.player.player_name)
    }

    def getClient(player: Player): Client = clients.get(player.player_name)
}
