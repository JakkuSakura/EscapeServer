import com.github.steveice10.mc.auth.exception.request.RequestException;
import com.github.steveice10.mc.protocol.MinecraftConstants;
import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.mc.protocol.data.SubProtocol;
import com.github.steveice10.mc.protocol.data.message.Message;
import com.github.steveice10.mc.protocol.data.status.ServerStatusInfo;
import com.github.steveice10.mc.protocol.data.status.handler.ServerInfoHandler;
import com.github.steveice10.mc.protocol.data.status.handler.ServerPingTimeHandler;
import com.github.steveice10.packetlib.Client;
import com.github.steveice10.packetlib.ProxyInfo;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.event.session.DisconnectedEvent;
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent;
import com.github.steveice10.packetlib.event.session.PacketSendingEvent;
import com.github.steveice10.packetlib.event.session.SessionAdapter;
import com.github.steveice10.packetlib.tcp.TcpSessionFactory;

import java.net.Proxy;
import java.util.Arrays;

public class TestClient {
    private static final boolean VERIFY_USERS = false;
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 45747;
    private static final ProxyInfo PROXY = null;
    private static final Proxy AUTH_PROXY = Proxy.NO_PROXY;
    private static final String USERNAME = "TestUser";
    private static final String PASSWORD = "********";

    public static void main(String[] args) {
        status();
        login();
    }

    private static void status() {
        MinecraftProtocol protocol = new MinecraftProtocol(SubProtocol.STATUS);
        Client client = new Client(HOST, PORT, protocol, new TcpSessionFactory(PROXY));
        client.getSession().setFlag(MinecraftConstants.AUTH_PROXY_KEY, AUTH_PROXY);
        client.getSession().setFlag(MinecraftConstants.SERVER_INFO_HANDLER_KEY, new ServerInfoHandler() {
            @Override
            public void handle(Session session, ServerStatusInfo info) {
                System.out.println("Version: " + info.getVersionInfo().getVersionName() + ", " + info.getVersionInfo().getProtocolVersion());
                System.out.println("Player Count: " + info.getPlayerInfo().getOnlinePlayers() + " / " + info.getPlayerInfo().getMaxPlayers());
                System.out.println("Players: " + Arrays.toString(info.getPlayerInfo().getPlayers()));
                System.out.println("Description: " + info.getDescription().getFullText());
                System.out.println("Icon: " + info.getIconPng());
            }
        });

        client.getSession().setFlag(MinecraftConstants.SERVER_PING_TIME_HANDLER_KEY, new ServerPingTimeHandler() {
            @Override
            public void handle(Session session, long pingTime) {
                System.out.println("Server ping took " + pingTime + "ms");
            }
        });

        client.getSession().connect();
        while(client.getSession().isConnected()) {
            try {
                Thread.sleep(5);
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void login() {
        MinecraftProtocol protocol = null;
        if(VERIFY_USERS) {
            try {
                protocol = new MinecraftProtocol(USERNAME, PASSWORD);
                System.out.println("Successfully authenticated user.");
            } catch(RequestException e) {
                e.printStackTrace();
                return;
            }
        } else {
            protocol = new MinecraftProtocol(USERNAME);
        }

        Client client = new Client(HOST, PORT, protocol, new TcpSessionFactory(PROXY));
        client.getSession().setFlag(MinecraftConstants.AUTH_PROXY_KEY, AUTH_PROXY);
        client.getSession().addListener(new SessionAdapter() {
            @Override
            public void packetSending(PacketSendingEvent event) {
                System.out.println("Sending message: " + event.getPacket());
            }

            @Override
            public void packetReceived(PacketReceivedEvent event) {
                System.out.println("Received message: " + event.getPacket());
            }

            @Override
            public void disconnected(DisconnectedEvent event) {
                System.out.println("Disconnected: " + Message.fromString(event.getReason()).getFullText());
                if(event.getCause() != null) {
                    event.getCause().printStackTrace();
                }
            }
        });

        client.getSession().connect();
    }
}