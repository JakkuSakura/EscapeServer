# Escape Server
This will be a handwritten minimalist Minecraft server but not all about Minecraft. I want it to be a playground for more stuff.

## Features
- [ ] Infinite world
- [ ] Dynamically generated random world
- [ ] Multiplayer
- [ ] High Performance

## Network Protocol
Protocol 578 of Minecraft 1.15.2

## Architecture
Cluster - Node(\*) - Worlds - Blocks/Entities

Worlds - Pages(16*16 blocks) - Chunk(16\*16\*256 blocks, using Quadtree and ) - Block

Message Queue

Multithreading

## Credits
Thank https://github.com/cnlohr/avrcraft for inspiration and simple implementation of Minecraft

Thank https://wiki.vg/ for reverse engineering of protocols

Thank https://github.com/barneygale/quarry/ for readable protocol examples

Thank https://github.com/GoLangMc/minecraft-server for a minimal working example

Thank https://github.com/ProtocolSupport/ProtocolSupport for block mappings

