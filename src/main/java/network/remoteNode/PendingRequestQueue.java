package network.remoteNode;

import network.exceptions.NetworkFailureException;
import network.message.reply.ReplyMessage;

import java.util.Calendar;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedDeque;

class PendingRequestQueue {

    private ConcurrentLinkedDeque<Request> pendingRequests = new ConcurrentLinkedDeque<>();
    private boolean closed = false;
    private static final long TIMEOUT=1000L;

    PendingRequestQueue(){
        Thread expiredRequestCollector=new Thread(this::expiredRequestCollector);
        expiredRequestCollector.setName("Expired request Collector");
        expiredRequestCollector.start();
    }

    void handleReplyMessage(ReplyMessage msg){
        Iterator<Request> iterator = pendingRequests.descendingIterator();
        Request req;
        while(iterator.hasNext()){
            req=iterator.next();
            if(req.getRequestId()==msg.getRequestId()){
                req.setReplyMessage(msg);
                return;
            }
        }
    }

    ReplyMessage submitRequest(Request request) throws NetworkFailureException {

        pendingRequests.addFirst(request);

        try {
            request.waitingLoop();
        } catch (InterruptedException e) {
            e.printStackTrace();
            pendingRequests.remove(request);
            return null;
        }

        pendingRequests.remove(request);

        if(request.isFailed())
            throw new NetworkFailureException();
        else
            return request.getReplyMessage();
    }

    void close(){
        closed=true;
        for(Request r : pendingRequests)
            r.delete();
    }

    private void expiredRequestCollector(){
        long currentTime;
        while(!closed){
            currentTime=Calendar.getInstance().getTimeInMillis();
            for(Request request : pendingRequests){
                if(currentTime-request.getTimestamp()>TIMEOUT)
                    request.delete();
            }
            try {
                Thread.sleep(TIMEOUT);
            } catch (InterruptedException e) {
                return;
            }
        }
    }


}
