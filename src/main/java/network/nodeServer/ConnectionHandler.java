package network.nodeServer;

import network.message.reply.ReplyMessage;
import network.message.reply.ResourceReply;
import network.message.request.RequestMessage;
import node.LocalNode;

import java.io.*;
import java.net.Socket;

class ConnectionHandler implements Closeable {

    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private final Socket socket;
    private final LocalNode localNode;
    private boolean closed=false;

    ConnectionHandler(Socket socket, LocalNode localNode) throws IOException {
        this.socket=socket;
        this.localNode=localNode;
        oos = new ObjectOutputStream(socket.getOutputStream());
        ois = new ObjectInputStream(socket.getInputStream());
    }

    void handle(){
        try {
            while (!closed) {
                RequestMessage in = (RequestMessage) ois.readObject();
                handleRequest(in);
            }
        } catch (EOFException e){
            close();
        } catch (IOException e) {
            e.printStackTrace();
            close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void handleRequest(RequestMessage msg) {
        try {
            ReplyMessage reply = msg.handleRequest(localNode);
            if(reply==null)
                return;
            reply.setRequestId(msg.getRequestId());
            oos.writeObject(reply);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        closed=true;
    }
}