package Core;

import ThreadPool.ThreadPool;
import ThreadPool.Task;
import UI.GUI;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Server {
// Server executing exception class:
    public static class ServerException extends Exception {
        private ServerExceptionType exceptionType;
        private String description;

        public ServerException(ServerExceptionType type, String dscr) {
            exceptionType = type;
            description = dscr;
        }
        // get exception message method
        public String getExceptionMessage() {
            return "Server exception of type: " + exceptionType.getValue() + ": " + description;
        }
    }
// Server exception type enum:
    public enum ServerExceptionType {
        CONFIGURATOR_EXCEPTION("CONFIGURATOR EXCEPTION"),
        PAIR_CONTROLLER_EXCEPTION("PAIR CONTROLLER EXCEPTION"),
        CLIENT_RESPONDER_EXCEPTION("CLIENT RESPONDER EXCEPTION");
        private String value;

        ServerExceptionType(String value) {
            this.value = value;
        }
        // get enum String value method
        String getValue() {
            return value;
        }
    }
// inner class represents Server configurator
    private class ServerConfigurator {
        // main configuration method
        private void config(int port, String dst) throws ServerException {
            // check directory consistence
            Path dirPath = Paths.get(dst);
            if (!Files.exists(dirPath)) {
                throw new ServerException(ServerExceptionType.CONFIGURATOR_EXCEPTION, "Files save directory - " + dst + " doesn't consist");
            }
            // init field after successful checking
            saveDstDir = dst;

            // try to create server socket
            ServerSocket socket;
            try {
                socket = new ServerSocket(port);
                System.out.println(socket.getInetAddress().toString());
            } catch (IOException e) {
                throw new ServerException(ServerExceptionType.CONFIGURATOR_EXCEPTION, "Can't create server socket on port - " + port);
            }
            // init field after successful checking
            serverSocket = socket;
        }
    }
// inner class represents controller of clients pairing
    private class ClientPairController extends Thread {
        private int currentClientId;

        // accept connection for new client method
        private Socket acceptClient() throws ServerException {
            Socket clientSocket;
            try {
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                throw new ServerException(ServerExceptionType.PAIR_CONTROLLER_EXCEPTION, "Can't accept connection for client: " + currentClientId);
            }
            return clientSocket;
        }
        // main executing method (clients pairing)
        @Override
        public void run() {
            while (!terminateExecution) {
                Socket clientSocket = null;
                try {
                    clientSocket = acceptClient();
                    gui.displayClientConnecting(currentClientId);
                } catch (ServerException e) {
                    gui.displayServerMessage(e.getExceptionMessage());
                    continue;
                }
                // create new task and add it to Thread Pool:
                Task newTask = new Task(clientSocket, saveDstDir, currentClientId, MAX_BUFFER_SIZE, gui);
                clientHandler.addTask(newTask);
                // increment current client id
                currentClientId++;
            }
        }
        // init lastClientId with start value
        private ClientPairController() {
            currentClientId = 0;
        }
    }
// fields:
    // GUI
    private GUI gui;
    // server socket which accepts clients connection
    private ServerSocket serverSocket;
    // files save destination directory
    private String saveDstDir;
    // implemented thread pool for client handling
    private ThreadPool clientHandler;

    private ServerConfigurator configurator;
    private ClientPairController pairController;

    // terminate serve execution flag
    private boolean terminateExecution;
    // maximum size of buffer (16kb)
    private static final int MAX_BUFFER_SIZE = 16000;
// constructor:
    public Server(GUI gui) {
        this.gui = gui;
        configurator = new ServerConfigurator();
        pairController = new ClientPairController();
        clientHandler = new ThreadPool(1);
        terminateExecution = false;
    }
// methods:
    // start server executing method
    public void execute(int port, String dst) {
        try {
            configurator.config(port, dst);
        }
        catch (ServerException e) {
            gui.displayServerMessage(e.getExceptionMessage());
            System.exit(1);
        }
        // start handle clients
        pairController.start();
    }
    // terminate server executing process method
    public void terminate() {}
}
