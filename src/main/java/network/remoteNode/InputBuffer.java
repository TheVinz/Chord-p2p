package network.remoteNode;

import network.message.reply.ReplyMessage;

import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


class InputBuffer {

    private final PendingRequestQueue queue;
    private final ExecutorService pool;
    private boolean closed=false;
    private final int id;

    InputBuffer(ObjectInputStream ois, PendingRequestQueue queue, int id) {
        this.queue=queue;
        this.id = id;
        pool = Executors.newSingleThreadExecutor();
        pool.submit(() -> loop(ois));
    }

    private void loop(ObjectInputStream ois){
        Thread.currentThread().setName("Node " + id + " input stream");
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
        pool.shutdown();
        closed=true;
    }
}
