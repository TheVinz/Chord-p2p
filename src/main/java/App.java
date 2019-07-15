import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import distributedDB.ResourceManager;
import network.exceptions.NetworkFailureException;
import network.nodeServer.NodeServer;
import node.StabilizerNode;
import resource.ChordResource;
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
import java.util.Scanner;


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
        ChordNetwork network = new ChordNetwork();
        if (join)
            joinChordNetwork(network);
        else
            startAnchorMode(network);

        evalInput(network);
    }

    private void joinChordNetwork(ChordNetwork network) {
        network.join(anchorHost, anchorPort, nodeHost, nodePort);
    }


    private void startAnchorMode(ChordNetwork network) throws NetworkFailureException {
        network.create(nodeHost, nodePort);
    }

    public static void main(String[] args) throws NetworkFailureException {
        App app = new App();
        JCommander parser = JCommander.newBuilder()
                .addObject(app)
                .build();

        parser.parse(args);

        if (app.help) {
            parser.usage();
            return;
        }


        NetworkSettings config = SettingsManager.getNetworkSettings();
        LogFormatter.logSetup(config.getLoggingLevel());

        app.run();
    }

    private static void evalInput(ChordNetwork network) {
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
                    ChordResource resource = remoteResource.fetch();
                    if (resource.isNotFound())
                        System.err.println("Resource [" + resource.getTitle() + "] not found.");
                    else
                        System.out.println(resource.getContent());
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

    /*private void startAnchorMode() throws NetworkFailureException {
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
    }*/

}
