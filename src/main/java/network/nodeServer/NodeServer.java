package network.nodeServer;

import node.LocalNode;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class NodeServer implements Closeable {
    private final LocalNode localNode;
    private final int port;
    private final String ip;
    private final ServerSocket ssocket;
    private boolean closed=false;

    public NodeServer(LocalNode localNode, String ip, int port) throws IOException {
        this.localNode=localNode;
        this.ip=ip;
        this.port=port;
        ssocket = new ServerSocket(port);
        new Thread(this::loop).start();
    }

    public void loop(){
        System.out.println("Node server listening on "+ip+":"+port);
        try {
            while (!closed) {
                Socket client = ssocket.accept();
                ConnectionHandler handler = new ConnectionHandler(client, localNode);
                new Thread(handler::handle).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    @Override
    public void close() throws IOException {
        closed=true;
        ssocket.close();
    }
}
