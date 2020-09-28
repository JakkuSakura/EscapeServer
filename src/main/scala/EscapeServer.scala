import io.netty.channel.ChannelHandlerContext
import scala.collection.concurrent

object EscapeServer extends Runnable {

    val clients = new concurrent.TrieMap[String, Client]
    var theThread: Thread = _

    def add_client(client: Client): Unit = clients(client.player.player_name) = client

    def removeClient(id: String): Unit = clients.remove(id)

    def playerUpdate(player: Player): Unit = {
        // TODO
    }

    def customPreloadStep(): Unit = {}

    def updateServer(): Unit = {
        val delta = 0x2faf080; // 50,000,000 nanoseconds (0.05 seconds)
        var server_time = System.nanoTime()
        while (System.nanoTime() - server_time > delta) {
            server_time += delta
            var local_player_count = 0
            clients.foreachEntry((_, x) => {
                val p = x.player
                if (p.active) local_player_count += 1
                else return
                p.update_number += 1
                //                    if (p.need_to_reply_to_ping) {
                //                        x.ctx.channel().writeAndFlush(new Pong(p.player_name).toByteBuf)
                //                        p.need_to_reply_to_ping = false
                //                    }
                //                    if (p.need_to_send_playerlist) {
                //                        x.ctx.channel().writeAndFlush(new PlayerList().toByteBuf)
                //                        p.need_to_send_playerlist = false
                //                    }
                //If we didn't finish getting all the broadcast data last time
                //We must do that now.
                if (p.has_logged_on) {
                    if (p.did_not_clean_out_broadcast_last_time)
                        println("did_not_clean_out_broadcast_last_time")
                    // TODO                            goto now_sending_broadcast;
                    else {
                        //From the game portion
                        playerUpdate(p)
                    }
                }
                //BIG NOTE:
                //Everything in here is SELECTIVE! This means it gets sent to the specific client
                //ready to receive data!
                //
                //Login process:
                //
                //For checking in with the server:  (p.need_to_send_playerlist) and that's about it.
                //For players who are actually joining the server:
                //	p.need_to_login then
                //	p.need_to_spawn then
                //  p.next_chunk_to_load = 1..? for all chunks*16, one per packet (needs to update rows). Then
                //	p.custom_preload_step then
                //   (Do your custom step)... Then, when YOU ARE DONE set
                //  p.need_to_send_lookupdate
                //   Now, it is listening to broadcast messages.
                //  Now, you're cooking!

                if (p.has_logged_on) {
                    //If we turn around too far, we MUST warp a reset, because if we get an angle too big,
                    //we overflow the angle in our 16-bit fixed point.

                    //It's too expensive to do the proper modulus on a floating point value.
                    //Additionally, we need to say stance++, otherwise we will fall through the ground when we turn around.
                    if (p.yaw < -11520) {
                        p.yaw += 11520
                        p.need_to_send_lookupdate = true
                        p.stance += 1
                    }
                    if (p.yaw > 11520) {
                        p.yaw -= 11520
                        p.need_to_send_lookupdate = true
                        p.stance += 1
                    }
                    if (p.y < 0) p.need_to_respawn = true
                }

                //I'm worried about things overflowing here, should we consider some mechanism to help prevent this?

                //I used to do things here, it's a useful place to stick things that need to happen every tick..
                //I don't really use it anymore
                if (p.tick_since_update) {
                    p.tick_since_update = false
                }
                if (p.need_to_respawn) {
                    p.x = 0
                    p.y = 64
                    p.stance = p.y; // + (1<<FIXEDPOINT);
                    p.z = 0
                    p.need_to_send_lookupdate = true
                    p.need_to_respawn = false
                }

                //                    if (p.need_to_send_lookupdate) {
                //                        x.ctx.channel().writeAndFlush(new LookUpdate(p).toByteBuf)
                //                        p.need_to_send_lookupdate = false;
                //                    }
                //                    //We're just logging in!
                //                    if (p.need_to_spawn) {
                //
                //                        //Newer versions need not send this, maybe?
                //                        x.ctx.channel().writeAndFlush(new WelcomeWorld(p).toByteBuf)
                //
                //                        p.need_to_spawn = false
                //
                //                        p.next_chunk_to_load = 0
                //                        p.has_logged_on = true
                //                        p.just_spawned = true;
                //
                //                        //For next time round we send to everyone
                //                        //    uint8_t i;
                //                        //    //Show us the rest of the players
                //                        //    for( i = 0; i < MAX_PLAYERS; i++ )
                //                        //    {
                //                        //        if( i != playerid && Players[i].active )
                //                        //        {
                //                        //            SSpawnPlayer( i );
                //                        //        }
                //                        //    }
                //
                //                    }

                if (p.custom_preload_step) {
                    customPreloadStep()
                    p.custom_preload_step = false
                    p.need_to_respawn = true
                    p.player_is_up_and_running = true
                    //This is when we checkin to the updates. (after we've sent the map chunk updates)
                    //                        p.outcirctail = GetCurrentCircHead();
                }

                //Send the client a couple chunks to load on.
                //Right now we just send a bunch of copy-and-pasted chunks.
                //                    if( p.next_chunk_to_load != 0)
                //                    {
                //                   //			p.custom_preload_step = 1;
                //                   //			p.next_chunk_to_load = 0;
                //
                //                        val pnc = p.next_chunk_to_load
                //                        p.next_chunk_to_load += 1
                //
                //                        if( pnc == 2 )
                //                        {
                //                            SendRawPGMData( compeddata, sizeof(compeddata) );
                //                        }
                //
                //                        int chk = pnc - 3;
                //                        if( chk == 16 )
                //                        {
                //                            p.next_chunk_to_load = 0;
                //                            p.custom_preload_step = 1;
                //                        }
                //                        else
                //                        {
                //                            int k = 0;
                //                            for( k = 0; k < 16; k++ )
                //                                SblockInternal( k, 63, chk, 2, 0 );
                //                        }
                //                    }

                /*
                //This is triggered when players want to actually join.
                if( p.need_to_login )
                {
                    StartSend();
                    Sbyte( 0x03 ); //Set compression threshold
                    Svarint( 1000 ); //Arbitrary, so we only hit it when we send chunks.
                    DoneSend();

                    p.set_compression = 1;

                    StartSend();
                    Sbyte( 0x02 ); //Login success
                    Suuid( playerid + PLAYER_LOGIN_EID_BASE );
                    p.need_to_login = 0;
                    Sstring( (const char*)p.playername, -1 );
                    DoneSend();

                    //Do this, it is commented out for other reasons.
                    p.need_to_spawn = 1;

                }


                if( p.need_to_send_keepalive )
                {

                    StartSend();
                    Sbyte( 0x1f );
                    Svarint( dumbcraft_tick );
                    DoneSend();
                    p.need_to_send_keepalive = 0;
                }


                if( p.has_logged_on && !p.doneupdatespeed )
                {
                    UpdatePlayerSpeed( p.running?RUNSPEED:WALKSPEED );
                    p.doneupdatespeed = 1;
                }

               now_sending_broadcast:
                       //Apply any broadcast messages ... if we just spawned, then there's nothing to send.
                       if( p.player_is_up_and_running )
                       {
                           p.did_not_clean_out_broadcast_last_time = UnloadCircularBufferOnThisClient( &p.outcirctail );
                       }

                       EndSend();
                   }
                   dumbcraft_playercount = localplayercount;
                */
            })
        }


        Thread.sleep(5L)
    }

    /*

    //This function should only be called ~10x/second
    void TickServer()
    {
        dumbcraft_tick++;

    #ifdef INCLUDE_ANNOUNCE_UTILS
        if( ( dumbcraft_tick & 0xf ) == 0 )
        {
            SendAnnounce( );
        }
    #endif

        //Everything in here should be broadcast to all players.
        StartupBroadcast();

        GameTick();

        for( playerid = 0; playerid < MAX_PLAYERS; playerid++ )
        {
            struct Player * p = &Players[playerid];
            if( !p->active ) continue;

            //Send a keep-alive every so often
            if( ( dumbcraft_tick & 0x2f ) == 0 )
            {
                p->need_to_send_keepalive = 1;
            }

            if( p->just_spawned )
            {
                p->just_spawned = 0;
                SSpawnPlayer( playerid );

                DoneBroadcast();
                p->outcirctail = GetCurrentCircHead(); //If we don't, we'll see ourselves.
                StartupBroadcast();
            }

            if( p->x != p->ox || p->y != p->oy || p->stance != p->os || p->z != p->oz )
            {
                int16_t diffx = p->x - p->ox;
                int16_t diffy = p->y - p->oy;
                int16_t diffz = p->z - p->oz;
                if( diffx < -127 || diffx > 127 || diffy < -127 || diffy > 127 || diffz < -127 || diffz > 127 )
                {
                    StartSend();
                    Sbyte( 0x49 );  //Updated (Teleport Entity)
                    Svarint( (uint8_t)(playerid + PLAYER_EID_BASE) );
                    Sdouble( p->x );
                    Sdouble( p->y );
                    Sdouble( p->z );
                    Sbyte( p->nyaw );
                    Sbyte( p->npitch );
                    Sbyte( ONGROUND );
                    DoneSend();
                }
                else
                {
                    StartSend();
                    Sbyte( 0x26 ); //Updated (Entity Look And Relative Move)
                    Svarint( (uint8_t)(playerid + PLAYER_EID_BASE) );
                    Sshort( diffx*128 );
                    Sshort( diffy*128 );
                    Sshort( diffz*128 );
                    Sbyte( p->nyaw );
                    Sbyte( p->npitch );
                    Sbyte( ONGROUND );
                    DoneSend();
                    p->op = p->pitch; p->ow = p->yaw;
                }

                StartSend();
                Sbyte( 0x34 ); //Updated (look at with head)
                Svarint( (uint8_t)(playerid + PLAYER_EID_BASE) );
                Sbyte( p->nyaw );
                DoneSend();

                p->ox = p->x;
                p->oy = p->y;
                p->oz = p->z;
                p->os = p->stance;
            }

            if( p->pitch != p->op || p->yaw != p->ow )
            {

                StartSend();
                Sbyte( 0x27 ); //Entity Look
                Svarint( (uint8_t)(playerid + PLAYER_EID_BASE) );
                Sbyte( p->nyaw );
                Sbyte( p->npitch );
                Sbyte( ONGROUND );
                DoneSend();

                StartSend();
                Sbyte( 0x34 ); //Updated (look at with head)
                Svarint( (uint8_t)(playerid + PLAYER_EID_BASE) );
                Sbyte( p->nyaw );
                DoneSend();

                p->op = p->pitch; p->ow = p->yaw;
            }

            p->tick_since_update = 1;

            PlayerTickUpdate( );
        }

        DoneBroadcast();

    }
     */

    override def run(): Unit = {
        while (true) {
            updateServer()
        }
    }

    def newThread(): Unit = {
        new Thread(EscapeServer).start()
    }
}
