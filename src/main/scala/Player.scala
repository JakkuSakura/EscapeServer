class Player {
    var view_distance: Int = 8

    var entity_id: Int = Configuration.newEntityId()
    var game_mode: Byte = 1
    // 0: Survival, 1: Creative, 2: Adventure, 3: Spectator. Bit 3 is the hardcore flag

    var demension: Int = 0
    // -1: Nether, 0: Overworld, 1: End; also, note that this is not aVarInt but instead a regular int

    var x, y, stance, z, yaw, pitch: Float = _
    var ox, oy, os, oz, ow, op: Int = _

    var n_yaw, n_pitch: Int = _
    var did_not_clean_out_broadcast_last_time: Boolean = _ //Didn't finish getting all the broadcast circular buffer cleared.
    //This prevents any 'update' code from happening next time round.
    var did_move: Boolean = _
    var did_pitchyaw: Boolean = _
    var just_spawned: Boolean = _

    var running: Boolean = _
    var done_update_speed: Boolean = _

    var on_ground: Boolean = _
    var active: Boolean = _
    var has_logged_on: Boolean = _

    var need_to_send_playerlist: Boolean = _
    var need_to_spawn: Boolean = _
    var need_to_login: Boolean = _
    var need_to_send_keepalive: Boolean = _
    var need_to_send_lookupdate: Boolean = _
    var need_to_reply_to_ping: Boolean = _
    var player_is_up_and_running: Boolean = _ //Sent after the custom preload is done.

    var next_chunk_to_load: Int = _
    var custom_preload_step: Boolean = _ //if nonzero, then do pre-load, when done, set to 0 and set p->need_to_send_lookupdate = 1;

    var need_to_respawn: Boolean = _
    var update_number: Int = _

    var set_compression: Boolean = _ //Once set, need to handle packets differently.

    var tick_since_update: Boolean = _
    var ticks_since_heard: Int = _
    var player_name: String = _
    var outcirctail: Int = _

    //	uint32_t keepalive_id; //can also be used for pinging
}
