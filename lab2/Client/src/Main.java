import Core.Client;
import Core.Client.Mode;
import UI.TextGUI;

public class Main {
    public static void main(String[] args) {
        int argsCount = 4;
        if (args.length != 4) {
            System.out.println("Invalid args count - " + args.length + ", should be " + argsCount);
            return;
        }
        // get server address:
        String address = args[0];

        // get server port:
        int port;
        String portStr = args[1];
        try {
            port = Integer.parseInt(portStr);
        }
        catch (NumberFormatException e) {
            System.out.println("invalid port number - " + portStr);
            return;
        }
        // get mode:
        String modeStr = args[2];
        Mode mode;
        if (modeStr.equals("TEST")) {
            mode = Mode.TEST;
        }
        else if (modeStr.equals("NORMAL")) {
            mode = Mode.NORMAL;
        }
        else {
            System.out.println("Invalid mode - " + modeStr + " should be 'TEST' or 'NORMAL'");
            return;
        }
        // get file path:
        String filePath = args[3];
        Client client = new Client(mode, filePath, address, port, new TextGUI());
        // run client
        client.execute();
    }
}
