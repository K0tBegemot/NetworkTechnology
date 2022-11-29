package Tools;
import Core.Server.ServerExceptionType;
import Core.Server.ServerException;
import ThreadPool.Data.DataTransferDescription;

import java.io.IOException;
import java.io.OutputStream;

public class ClientNotifier {
    private OutputStream socketOutputStream;
    // specified socket's output stream
    public ClientNotifier(OutputStream outputStream) {
        assert outputStream != null;
        socketOutputStream = outputStream;
    }
    public void notifyClient(DataTransferDescription description) throws ServerException {
        String message = description.getDescriptionMessage();
        byte[] messageData = message.getBytes();

        try {
            socketOutputStream.write(messageData);
        } catch (IOException e) {
            throw new ServerException(ServerExceptionType.CLIENT_RESPONDER_EXCEPTION, "can't write response to client");
        }
    }
}
