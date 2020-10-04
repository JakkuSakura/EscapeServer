package player

import utils.Configuration

object GameMode extends Enumeration {
    val Survival, Creative, Adventure, Spectator = Value
    // 0: Survival, 1: Creative, 2: Adventure, 3: Spectator. Bit 3 is the hardcore flag
}

object Dimension extends Enumeration(initial = -1) {
    val Nether, Overworld, End = Value
    // -1: Nether, 0: Overworld, 1: End; also, note that this is not aVarInt but instead a regular int
}

class Player {
    var hp: Float = 10.0f

    var view_distance: Int = 8

    var entity_id: Int = Configuration.newEntityId()
    var game_mode: GameMode.Value = GameMode.Creative

    var dimension: Dimension.Value = Dimension.Overworld

    var x, y, stance, z, yaw, pitch: Float = _
    var ox, oy, os, oz, ow, op: Int = _

    var n_yaw, n_pitch: Int = _

    var did_move: Boolean = _
    var did_pitchyaw: Boolean = _

    var just_spawned: Boolean = _

    var running: Boolean = _
    var done_update_speed: Boolean = _

    var on_ground: Boolean = _
    var active: Boolean = _
    var has_logged_on: Boolean = _

    var player_is_up_and_running: Boolean = _ //Sent after the custom preload is done.

    var next_chunk_to_load: Int = _

    var need_to_respawn: Boolean = _
    var update_number: Int = _

    var tick_since_update: Boolean = _
    var ticks_since_heard: Int = _
    var player_name: String = _

    //	uint32_t keepalive_id; //can also be used for pinging
}
