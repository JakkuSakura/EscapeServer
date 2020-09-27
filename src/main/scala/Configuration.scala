import java.util.concurrent.atomic.AtomicInteger

object Configuration {

    val WORLDTYPE = 0 //Overworld
    val GAMEMODE = 0  //creative
    val DIFFICULTY = 0 // peaceful
    val MAX_PLAYERS = 10

    val MAPSIZECHUNKS = 1

    val RUNSPEED = 5
    val WALKSPEED = 3

    val SENDBUFFERSIZE = 80
    val PLAYER_EID_BASE = 0x20
    val PLAYER_LOGIN_EID_BASE = 0x40
    val MAX_CHATLEN = 100
    var player_id_count = new AtomicInteger(0)

    def newPlayerId(): Int = {
        player_id_count.getAndIncrement()
    }
}
