import Core.Server;
import UI.TextGUI;

public class Main {
    public static void main(String[] args) {
        int argsCount = 2;
        if (args.length != argsCount) {
            System.out.println("Invalid args count - " + args.length + ", should be " + argsCount);
            return;
        }
        // get dst dir path:
        String path = args[0];
        // get port number
        String portStr = args[1];
        int port;
        try {
            port = Integer.parseInt(portStr);
        }
        catch (NumberFormatException e) {
            System.out.println("Invalid port - " + portStr);
            return;
        }
        Server server = new Server(new TextGUI());
        server.execute(port, path);
    }
}
