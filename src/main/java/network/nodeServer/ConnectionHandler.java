package network.nodeServer;

import network.exeptions.NetworkFailureException;
import network.message.Message;
import network.message.ReplyMessage;
import network.message.RequestMessage;
import network.remoteNode.RemoteNode;
import node.Node;
import node.exceptions.FingerTableEmptyException;
import node.exceptions.NodeNotFoundException;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

class ConnectionHandler implements Closeable {

    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private final Socket socket;
    private final Node localNode;
    private boolean closed=false;

    ConnectionHandler(Socket socket, Node localNode) {
        this.socket=socket;
        this.localNode=localNode;
        try{
            ois = new ObjectInputStream(socket.getInputStream());
            oos = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            try {
                close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            closed=true;
        }
    }

    void handle(){
        try {
            while (!closed) {
                Message in = (Message) ois.readObject();
                if(in.getClass()==RequestMessage.class){
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
            Node node;
            ReplyMessage reply;
            switch (msg.method) {
                case Message.FIND_SUCCESSOR:
                    node = localNode.findSuccessor(msg.id, null);
                    reply = new ReplyMessage(msg.method, node.getIp(), node.getPort(), node.getId());
                    reply.setRequestId(msg.getRequestId());
                    oos.writeObject(reply);
                    break;
                case Message.GET_PREDECESSOR:
                    node=localNode.getPredecessor();
                    reply = new ReplyMessage(msg.method, node.getIp(), node.getPort(), node.getId());
                    reply.setRequestId(msg.getRequestId());
                    oos.writeObject(reply);
                    break;
                case Message.GET_SUCCESSOR:
                    node = localNode.getSuccessor();
                    reply = new ReplyMessage(msg.method, node.getIp(), node.getPort(), node.getId());
                    reply.setRequestId(msg.getRequestId());
                    oos.writeObject(reply);
                    break;
                case Message.NOTIFY_PREDECESSOR:
                    //TODO: Ricordarsi di aprire la connessione con il remote node o qui o nel localNode
                    node = new RemoteNode(msg.id, msg.ip, msg.port);
                    localNode.notifyPredecessor(node);
                    break;
            }
        } catch (NodeNotFoundException e) {
            e.printStackTrace();
        } catch (FingerTableEmptyException e) {
            e.printStackTrace();
        } catch (NetworkFailureException e) {
            //TODO: La NetworkFailureException non dovrebbe essere mai lanciata dal localNode
            e.printStackTrace();
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
