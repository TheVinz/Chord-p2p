package network.nodeServer;

import node.LocalNode;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NodeServer implements Closeable {
    private static final Logger LOGGER = Logger.getLogger(NodeServer.class.getSimpleName());
    private final LocalNode localNode;
    private final ServerSocket ssocket;
    private final ExecutorService pool;
    private boolean closed = false;

    public NodeServer(LocalNode localNode, int port) throws IOException {
        this.localNode = localNode;
        pool = Executors.newFixedThreadPool(20);
        try {
            ssocket = new ServerSocket(port);
            LOGGER.log(Level.INFO, "Listening on port {0}", port);
        } catch(IOException e) {
            LOGGER.log(Level.SEVERE, "Cannot bind on port {0}", port);
            close();
            throw e;
        }
    }

    public void loop() {
        try {
            while (!closed) {
                Socket client = ssocket.accept();
                LOGGER.log(Level.FINE, "New client {0} accepted on port {1}",
                        new Object[]{client.getRemoteSocketAddress(), client.getLocalPort()});

                ConnectionHandler handler = new ConnectionHandler(client, localNode);
                pool.submit(handler::handle);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        } finally {
            close();
        }
    }

    @Override
    public void close() {
        if (!closed) {
            closed = true;
            try {
                ssocket.close();
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Troubles in closing the server socket", e);
            } finally {
                pool.shutdown();
            }
            LOGGER.log(Level.INFO, "Server closed");
        }
    }

    public boolean isClosed() {
        return closed;
    }
}
