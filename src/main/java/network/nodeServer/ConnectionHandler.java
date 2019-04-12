package network.nodeServer;

import network.message.Message;
import network.message.reply.ReplyMessage;
import network.message.request.RequestMessage;
import node.LocalNode;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

class ConnectionHandler implements Closeable {

    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private final Socket socket;
    private final LocalNode localNode;
    private boolean closed=false;

    ConnectionHandler(Socket socket, LocalNode localNode) {
        this.socket=socket;
        this.localNode=localNode;
        try{
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            try {
                close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    void handle(){
        try {
            while (!closed) {
                Message in = (Message) ois.readObject();
                if(in instanceof RequestMessage){
                    new Thread(
                            () -> handleRequest((RequestMessage) in)
                    ).start();
                }
                else {
                    //TODO: verificare che effettivamente questa cosa non succede mai
                    System.err.println("Questa cosa in teoria non dovrebbe succedere\nBy Vinz\nScottigay");
                }
            }
        } catch (IOException e) {
            try {
                close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void handleRequest(RequestMessage msg) {
        try {
            ReplyMessage reply = msg.handleRequest(localNode);
            if(reply!=null) {
                synchronized (this) {
                    oos.writeObject(reply);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() throws IOException {
        ois.close();
        oos.close();
        socket.close();
        closed=true;
    }
}
