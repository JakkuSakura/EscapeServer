package game

import block.Stone
import player.Player

class CommandHandler(/*server: Server*/) {
    def process(player: Player, command: String): Unit = {
        command.split(" ")(0) match {
            case "/set" =>
                println("Processing /set")
                Server.world.setBlock(player.x.round, player.y.toInt, player.z.toInt, new Stone())
                Server.sendNearbyChunks(player, false)
        }
    }
}
