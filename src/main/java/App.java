import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import network.nodeServer.NodeServer;
import node.StabilizerNode;
import resource.RemoteResource;
import utils.LogFormatter;

import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;

import static utils.Util.createDefaultStabilizerNode;

public class App {

    @Parameter(names = {"--anchor-host", "-a"}, description = "Host of the node to join")
    private String anchorHost = "localhost";

    @Parameter(names = {"--anchor-port", "-p"}, description = "Port of the node to join")
    private int anchorPort = 8888;

    @Parameter(names = {"--local-host", "-l"}, description = "Host of the current node")
    private String nodeHost = "localhost";

    @Parameter(names = {"--local-port", "-P"}, description = "Port of the current node",
            password = true, echoInput = true)
    private int nodePort = 8888;

    @Parameter(names = {"--ring-size", "-m"}, description = "Dimension of the chord ring (m coeff.)")
    private int m = 8;

    @Parameter(names = {"--num-succs", "-r"}, description = "Number of successors (r coeff.)")
    private int r = 4;

    @Parameter(names = {"--help", "-h"}, help = true)
    private boolean help = false;

    @Parameter(names = {"--join", "-j"})
    private boolean join = false;

    private void run() {
        if (join)
            joinChordNetwork();
        else
            startAnchorMode();
    }

    private void joinChordNetwork() {
        System.out.println(nodePort);
        LogFormatter.logSetup(Level.FINE);

        ChordNetwork network = new ChordNetwork();
        network.join(anchorHost, anchorPort, nodeHost, nodePort);

        Scanner sc = new Scanner(System.in);
        while (!network.isClosed()) {
            String input = sc.next(), name, content;
            switch (input) {
                case "publish":
                    name = sc.next();
                    content = sc.next();
                    network.publish(name, content);
                    break;
                case "find":
                    name = sc.next();
                    RemoteResource remoteResource = network.find(name);
                    System.out.println(remoteResource.fetch().getContent());
                    break;
                case "dump":
                    network.dumpNode();
                    break;
            }
        }

        sc.close();
    }

    private void startAnchorMode() {
        LogFormatter.logSetup(Level.FINER);

        int id = ChordNetwork.calculateDigest(nodeHost + ":" + nodePort);

        StabilizerNode anchor =
                createDefaultStabilizerNode(id, nodeHost, nodePort, new long[]{500, 800, 1000, 1000}, new long[]{250, 250, 250, 250});
        anchor.start();
        try(NodeServer server = new NodeServer(anchor, nodePort)) {
            server.loop();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            anchor.stop();
        }
    }

    public static void main(String[] args) {
        App app = new App();
        JCommander parser = JCommander.newBuilder()
                .addObject(app)
                .build();

        parser.parse(args);

        if (app.help) {
            parser.usage();
            return;
        }
        app.run();
    }
}
