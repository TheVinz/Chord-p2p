package network.remoteNode;

import network.message.reply.ReplyMessage;

import java.io.*;


class InputBuffer {

    private final PendingRequestQueue queue;
    private boolean closed=false;

    InputBuffer(ObjectInputStream ois, PendingRequestQueue queue, int id) {
        this.queue=queue;
        Thread thread = new Thread(() -> loop(ois));
        thread.setName("Node " + id + " input stream");
        thread.start();
    }

    private void loop(ObjectInputStream ois){
        try {
            while (!closed) {
                ReplyMessage in = (ReplyMessage) ois.readObject();
                queue.handleReplyMessage(in);
            }
        } catch (IOException e) {
            close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void close() {
        queue.close();
        closed=true;
    }
}
