package UI;

import Core.Client;

// text GUI implementation
public class TextGUI implements GUI {
    @Override
    public synchronized void displayConnectionMessage(String serverAddress, int port) {
        System.out.println("STATUS: Client connected successfully; ip - " + serverAddress + ", port - " + port);
    }

    @Override
    public synchronized void displayClientException(Client.ClientException e) {
        System.out.println("STATUS: " + e.getExceptionMessage());
    }

    @Override
    public synchronized void displayServerMessage(String message) {
        System.out.println("SERVER RESPONSE: " + message);
    }
}
