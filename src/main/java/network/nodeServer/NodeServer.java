package network.nodeServer;

import network.exeptions.NetworkFailureException;
import node.Node;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class NodeServer implements Closeable {
    private final Node localNode;
    private final int port;
    private final String ip;
    private final ServerSocket ssocket;
    private boolean closed=false;

    //TODO: forse meglio se localNode fosse istanza di LocalNode
    public NodeServer(Node localNode, String ip, int port) throws IOException {
        this.localNode=localNode;
        this.ip=ip;
        this.port=port;
        ssocket = new ServerSocket(port);
    }

    public void loop(){
        try {
            while (!closed) {
                Socket client = ssocket.accept();
                new ConnectionHandler(client, localNode).handle();
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
