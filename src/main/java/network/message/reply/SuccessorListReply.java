package network.message.reply;

import com.sun.xml.internal.messaging.saaj.util.FinalArrayList;
import node.Node;

import java.util.List;

public class SuccessorListReply extends ReplyMessage{

    private final List<Integer> ids = new FinalArrayList();
    private final List<String> ips = new FinalArrayList();
    private final List <Integer> ports = new FinalArrayList();

    public SuccessorListReply(List<Node> successorsList){
        for(int i=0; i<successorsList.size(); i++){
            ids.add(successorsList.get(i).getId());
            ips.add(successorsList.get(i).getIp());
            ports.add(successorsList.get(i).getPort());
        }
    }


    public List<Integer> getIds() {
        return ids;
    }

    public List<String> getIps() {
        return ips;
    }

    public List<Integer> getPorts() {
        return ports;
    }
}
