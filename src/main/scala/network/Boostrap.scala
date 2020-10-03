package network

import game.Server
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel

class Boostrap {
    def bind(host: String, port: Int): Unit = {
        //配置服务端线程池组
        //用于服务器接收客户端连接
        val bossGroup = new NioEventLoopGroup()
        //用户进行SocketChannel的网络读写
        val workerGroup = new NioEventLoopGroup()

        try {
            //是Netty用户启动NIO服务端的辅助启动类，降低服务端的开发复杂度
            val bootstrap = new ServerBootstrap()
            //将两个NIO线程组作为参数传入到ServerBootstrap
            bootstrap.group(bossGroup, workerGroup)
              //创建NioServerSocketChannel
              .channel(classOf[NioServerSocketChannel])
              //绑定I/O事件处理类
              .childHandler(new ChannelInitializer[SocketChannel] {
                  override def initChannel(ch: SocketChannel): Unit = {
                      ch.pipeline().addLast(new PacketSplitter())
                      ch.pipeline().addLast(new InClientHandler())
                      ch.pipeline().addLast(new PacketEncoder())
                      println(ch.pipeline())
                  }
              })
            val channelFuture = bootstrap.bind(host, port).sync()
            channelFuture.channel().closeFuture().sync()
        } finally {
            bossGroup.shutdownGracefully()
            workerGroup.shutdownGracefully()
        }
    }
}


object Boostrap {

    def main(args: Array[String]): Unit = {
        Server.start()
        MessageSender.start()

        val server = new Boostrap()
        // Minecraft default port
        server.bind("0.0.0.0", 25565)
    }
}