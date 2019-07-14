package network.nodeServer;

import network.message.reply.ReplyMessage;
import network.message.request.RequestMessage;
import node.LocalNode;
import utils.NetworkSettings;
import utils.SettingsManager;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

class ConnectionHandler implements Closeable {

    private static final Logger LOGGER = Logger.getLogger(ConnectionHandler.class.getSimpleName());

    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private final Socket socket;
    private final LocalNode localNode;
    private final ExecutorService pool;
    private boolean closed=false;

    private NetworkSettings networkSettings = SettingsManager.getNetworkSettings();

    ConnectionHandler(Socket socket, LocalNode localNode) throws IOException {
        this.socket = socket;
        this.localNode = localNode;
        oos = new ObjectOutputStream(socket.getOutputStream());
        ois = new ObjectInputStream(socket.getInputStream());
        pool = Executors.newFixedThreadPool(20);
    }

    void handle(){
        try {
            while (!closed) {
                RequestMessage in = (RequestMessage) ois.readObject();
                //LOGGER.log(Level.FINER, "New message in: {0}", in.getClass().getSimpleName());

                pool.submit(() -> handleRequest(in));
            }
        } catch (IOException e){
            close();
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.WARNING,
                    "Unrecognised object was sent. Objects must be `RequestMessage`", e);
        }
    }

    private void handleRequest(RequestMessage msg) {
        try {
            if(networkSettings.isDelay()) {
                Thread.sleep(networkSettings.getDelay());
            }
            ReplyMessage reply = msg.handleRequest(localNode);
            if(reply==null)
                return;
            reply.setRequestId(msg.getRequestId());
            synchronized (this) {
                oos.writeObject(reply);
                oos.flush();
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Troubles in sending out the reply: ", e.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        if (!closed) {
            String
                    addr = socket.getRemoteSocketAddress().toString(),
                    port = String.valueOf(socket.getPort());
            try {
                ois.close();
                oos.close();
                socket.close();
                LOGGER.log(Level.FINE, "Connection {0}:{1} in closed",
                        new Object[]{addr, port});
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Trouble in closing the socket", e);
            } finally {
                closed = true;
                pool.shutdown();
            }
        }
    }
}