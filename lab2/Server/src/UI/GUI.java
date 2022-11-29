package UI;
import ThreadPool.Data.DataTransferDescription;

public interface GUI {
    void displayTraverseStatus(DataTransferDescription description, int clientId);
    void displayClientConnecting(int clientId);
    void displayClientDisconnecting(int clientId);
    void displayDataTraverseSpeed(long speedAverageCurrent, long speedAverageSession, int clientId);
    void displayServerMessage(String message);
}
