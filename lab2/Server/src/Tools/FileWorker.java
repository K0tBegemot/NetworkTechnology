package Tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileWorker {
    public static class FileWorkerException extends Exception {
        private Type type;
        public enum Type {FILE_CREATE, OFS_GET, FILE_WRITE, FILE_CLOSE, FILE_DELETE}
        FileWorkerException(Type t) {type = t;}
        public Type getType() {return type;}
    }
    // dst file to write data in
    private File outputFile;
    // file output stream
    private FileOutputStream outputStream;

    // Constructor creates new file in specified directory and init file writer
    public FileWorker(String fileName) throws FileWorkerException {
        outputFile = new File(fileName);
        // try create File in directory:
        try {
            if (!outputFile.createNewFile()) {
                // file is already exists
                //System.out.println("!!!");
                throw new FileWorkerException(FileWorkerException.Type.FILE_CREATE);
            }
        }
        catch (IOException e) {
            throw new FileWorkerException(FileWorkerException.Type.FILE_CREATE);
        }
        // try get file output stream:
        try {
            outputStream = new FileOutputStream(outputFile);
        }
        catch(Exception e) {
            throw new FileWorkerException(FileWorkerException.Type.OFS_GET);
        }
    }
    // write data to file method
    //  buffer - byte array which will be written to file
    //  offset - start byte idx; count - count of bytes to write
    public void writeData(byte[] buffer, int offset, int count) throws FileWorkerException {
        // use try-with-resources statement
        try {
            outputStream.write(buffer, offset, count);
        } catch (IOException e) {
            throw new FileWorkerException(FileWorkerException.Type.FILE_WRITE);
        }
    }
    // release file resources
    public void close() throws FileWorkerException {
        try {
            outputStream.close();
        } catch (IOException e) {
            throw new FileWorkerException(FileWorkerException.Type.FILE_CLOSE);
        }
    }
    // delete file from directory
    public void deleteFile() throws FileWorkerException{
        try {
            if (!outputFile.delete()) {
                throw new FileWorkerException(FileWorkerException.Type.FILE_DELETE);
            }
        }
        catch (SecurityException e) {
            throw new FileWorkerException(FileWorkerException.Type.FILE_DELETE);
        }
    }
}
