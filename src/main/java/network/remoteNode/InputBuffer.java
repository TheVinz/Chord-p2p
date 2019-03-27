package network.remoteNode;

import network.message.Message;
import network.message.ReplyMessage;

import java.io.*;
import java.net.Socket;


class InputBuffer implements Closeable {

    private final PendingRequestQueue queue;
    private boolean closed=false;

    InputBuffer(Socket socket, PendingRequestQueue queue) throws IOException {
        this.queue=queue;
        ObjectInputStream ois=new ObjectInputStream(socket.getInputStream());
        new Thread(() -> loop(ois)).start();
    }

    private void loop(ObjectInputStream ois){
        try {
            while (!closed) {
                Message in = (Message) ois.readObject();
                if (in.getClass() == ReplyMessage.class) {
                    queue.handleReplyMessage((ReplyMessage) in);
                } else {
                    //TODO: verificare che effettivamente questa cosa non succede mai
                    System.err.println("Questa cosa in teoria non dovrebbe succedere\nBy Vinz\nScottigay");
                }
            }
        } catch (IOException e) {
            close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void close() {
        closed=true;
    }
}
