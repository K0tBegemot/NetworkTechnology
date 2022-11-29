package ThreadPool;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;

import Core.Server.ServerException;
import ThreadPool.Data.TraverseStatus;
import ThreadPool.Data.DataTransferDescription;
import ThreadPool.Data.DataHeader;
import Tools.ClientNotifier;
import Tools.FileWorker;
import Tools.HeaderReader;
import UI.GUI;

public class Task implements Runnable {
// fields:
    // client socket
    private Socket clientSocket;
    // I/O socket streams
    private InputStream socketInStream;
    private OutputStream socketOutStream;
    // timeout of data transfer speed checking (ms)
    private final int checkSpeedTimeout = 3000;

    // const to convert milliseconds into seconds
    private final long milSecNumber = 1000;
    // the output dir name, data will save here
    private String outputDirName;
    // unique id of paired client
    private int clientId;
    // maximum buffer size
    private int maxBufferSize;
    // maximum file header size (in bytes)
    private final int maxHeaderSize = 1000;

    // total read file size
    private long totalFileSize;
    // expected file size
    private long expectedFileSize;
    // GUI
    private GUI gui;
// constructor:
    public Task(Socket socket, String fileName, int clientId, int maxBufferSize, GUI gui) {
        clientSocket = socket;
        outputDirName = fileName;
        this.clientId = clientId;
        this.maxBufferSize = maxBufferSize;
        this.gui = gui;
        socketInStream = null;
        socketOutStream = null;
    }
    // complete task execution method
    private void completeTaskExecution(DataTransferDescription traverseDescription) {
        DataTransferDescription resultDescription = traverseDescription;

        if (traverseDescription.getStatus() == TraverseStatus.SUCCESS_TRAVERSE) {
            // check equality of expected file size and actual file size
            if (expectedFileSize != totalFileSize) {
                resultDescription = new DataTransferDescription(TraverseStatus.INVALID_FILE_DATA,
                        "wrong actual file size:" + totalFileSize + ", expected: " + expectedFileSize);
            }
        }
        ClientNotifier notifier = new ClientNotifier(socketOutStream);
        try {
            // notify client
            notifier.notifyClient(resultDescription);
        } catch (ServerException e) {
            gui.displayServerMessage(e.getExceptionMessage());
        }
        // display data transfer status on Server side
        gui.displayTraverseStatus(resultDescription, clientId);
        // close connection:
        try {
            clientSocket.close();
        } catch (IOException e) {
            gui.displayServerMessage("can't close socket for client " + clientId);
        }
        gui.displayClientDisconnecting(clientId);
    }
    // get header data buffer; returns actual read byte count
    private int writeHeaderDataBuffer(byte[] buffer) throws DataTransferDescription {
        HeaderReader reader = new HeaderReader(maxHeaderSize, socketInStream);
        int dataSize = 0;
        try {
            dataSize = reader.readHeader(buffer);
        }
        catch (HeaderReader.HeaderReaderException e) {
            switch (e.getType()) {
                case IO:
                    throw new DataTransferDescription(TraverseStatus.INVALID_FILE_HEADER, "exception while header reading");
                case FEW_DATA:
                    throw new DataTransferDescription(TraverseStatus.INVALID_FILE_HEADER, "no input data");
                default:
                    assert false;
            }
        }
        return dataSize;
    }
    // get file header method (ALL input buffer contains data)
    private DataHeader getHeader(byte[] buffer) throws DataTransferDescription {
        assert buffer != null;

        DataHeader header = null;
        try {
            header = new DataHeader(buffer);
        } catch (DataHeader.HeaderException e) {
            // complete task execution in case of invalid header:
            switch(e.getType()) {
                case NAME:
                    throw new DataTransferDescription(TraverseStatus.INVALID_FILE_HEADER, "invalid file name");
                case LENGTH:
                    throw new DataTransferDescription(TraverseStatus.INVALID_FILE_HEADER, "invalid file length");
                case STRUCTURE:
                    throw new DataTransferDescription(TraverseStatus.INVALID_FILE_HEADER, "invalid header structure");
                default:
                    assert false;
            }
        }
        return header;
    }
    // write received data to file method (returns total recieved data size)
    private long writeFileData(byte[] buffer, byte[] remainData, String fileName) throws DataTransferDescription {
        long totalDataSize = remainData.length;
        try {
            FileWorker worker = new FileWorker(fileName);
            // write remain data
            worker.writeData(remainData, 0, remainData.length);

            long prevTime = System.currentTimeMillis();
            long startSessionTime = prevTime;
            long currentTime;
            long lastCheckDataSize = 0;

            // read file data from socket:
            while (true) {
                int readByteCount;
                try {
                    readByteCount = socketInStream.read(buffer);
                    // the stream is over
                    if (readByteCount < 0) {
                        break;
                    }
                    totalDataSize += readByteCount;
                    currentTime = System.currentTimeMillis();
                    //System.out.println(Long.toString(currentTime - prevTime));
                    if (currentTime - prevTime >= checkSpeedTimeout) {
                        long averageSessionSpeed = totalDataSize / ((currentTime - startSessionTime) / milSecNumber);
                        long averageCurrentSpeed = (totalDataSize - lastCheckDataSize) / ((currentTime - prevTime) / milSecNumber);

                        // display speeds:
                        gui.displayDataTraverseSpeed(averageCurrentSpeed, averageSessionSpeed, clientId);
                        // update prev time
                        prevTime = currentTime;
                        lastCheckDataSize = totalDataSize;
                    }

                    worker.writeData(buffer, 0, readByteCount);
                } catch (IOException e) {
                    throw new DataTransferDescription(TraverseStatus.SOCKET_EXCEPTION, "can't read data from socket");
                }
            }
            // release file resources
            worker.close();
        } catch (FileWorker.FileWorkerException e) {
            switch (e.getType()) {
                case OFS_GET:
                    throw new DataTransferDescription(TraverseStatus.OUTPUT_FILE_EXCEPTION, "Can't get output file stream");
                case FILE_CLOSE:
                    throw new DataTransferDescription(TraverseStatus.OUTPUT_FILE_EXCEPTION, "Can't close output file stream");
                case FILE_WRITE:
                    throw new DataTransferDescription(TraverseStatus.OUTPUT_FILE_EXCEPTION, "Can't write data to output file");
                case FILE_CREATE:
                    throw new DataTransferDescription(TraverseStatus.OUTPUT_FILE_EXCEPTION, "Can't create output file");
                case FILE_DELETE:
                    throw new DataTransferDescription(TraverseStatus.OUTPUT_FILE_EXCEPTION, "Can't delete output file");
                default:
                    assert false;
            }
        }
        return totalDataSize;
    }
    // main task executing method
    @Override
    public void run() {
        assert clientSocket.isConnected();
        // try get socket I/O streams
        try {
            socketInStream = clientSocket.getInputStream();
            socketOutStream = clientSocket.getOutputStream();
        } catch (IOException e) {
            completeTaskExecution(new DataTransferDescription(TraverseStatus.SOCKET_EXCEPTION, "Can't create socket I/O stream"));
            return;
        }
        // init variables:
        byte[] receiveDataBuffer = new byte[maxBufferSize];
        byte[] initDataBuffer;
        DataHeader header;

        // header fields values variables:
        int headerSize;
        String outputFileName;

        int dataSize;
        try {
            // read data header: file name + expected file size:
            dataSize = writeHeaderDataBuffer(receiveDataBuffer);
            header = getHeader(Arrays.copyOf(receiveDataBuffer, dataSize));

            // get header fields:
            headerSize = header.getHeaderSize();
            System.out.println(Integer.toString(dataSize) + " " + Integer.toString(headerSize));
            expectedFileSize = header.getExpectedFileSize();
            outputFileName = outputDirName + "/" + header.getFileName();
            // get remain data from buffer
            initDataBuffer = Arrays.copyOfRange(receiveDataBuffer, headerSize, dataSize);

            // write data to output file
            totalFileSize = writeFileData(receiveDataBuffer, initDataBuffer, outputFileName);
        }
        catch (DataTransferDescription description) {
            completeTaskExecution(description);
            return;
        }
        // complete task execution with success status
        completeTaskExecution(new DataTransferDescription(TraverseStatus.SUCCESS_TRAVERSE, "successful data traverse"));
    }
}
