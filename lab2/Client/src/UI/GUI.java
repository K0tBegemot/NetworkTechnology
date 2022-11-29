package UI;

import Core.Client.ClientException;

public interface GUI {
    // client connected successfully
    void displayConnectionMessage(String serverAddress, int port);
    // client execution threw an exception
    void displayClientException(ClientException e);
    // display server response
    void displayServerMessage(String message);
}
