import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import distributedDB.ResourceManager;
import network.exceptions.NetworkFailureException;
import network.nodeServer.NodeServer;
import node.StabilizerNode;
import resource.RemoteResource;
import utils.LogFormatter;
import utils.NetworkSettings;
import utils.SettingsManager;
import utils.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;

import static utils.ResourceUtil.createDefaultResourceManager;
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

    private void run() throws NetworkFailureException {
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
                default:
                    System.err.printf("Command `%s` not found\n", input);
            }
        }

        sc.close();
    }

    private void startAnchorMode() throws NetworkFailureException {
        LogFormatter.logSetup(Level.FINER);

        int id = Util.calculateDigest(nodeHost + ":" + nodePort);
        NetworkSettings config = SettingsManager.getNetworkSettings();

        ResourceManager resourceManager = createDefaultResourceManager(new long[]{1000, 1000}, new long[]{1000, 1000});

        StabilizerNode anchor = createDefaultStabilizerNode(id, nodeHost, nodePort,
                        config.getRoutineDelays(), config.getRoutinePeriods(), resourceManager);
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
        App protocolMain = new App();
        JCommander parser = JCommander.newBuilder()
                .addObject(protocolMain)
                .build();

        parser.parse(args);

        if (protocolMain.help) {
            parser.usage();
            return;
        }
        try {
            protocolMain.run();
        } catch (NetworkFailureException e) {
            e.printStackTrace();
        }
    }
}
