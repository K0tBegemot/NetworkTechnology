package Tools;

import java.io.IOException;
import java.io.InputStream;

public class HeaderReader {
// static nested exception class
    public static class HeaderReaderException extends Exception {
        private Type type;
        public enum Type {IO, FEW_DATA}
        HeaderReaderException(Type t) {type = t;}
        public Type getType() {return type;}
    }
    // fields:
    private int maxHeaderSize;          // max size of header in bytes
    private InputStream inputStream;    // input stream to read data from
// constructor
    public HeaderReader(int maxSize, InputStream input) {
        assert maxSize > 0;
        assert input != null;

        maxHeaderSize = maxSize;
        inputStream = input;
    }
    // read header method (returns total data size in buffer - header + other data)
    public int readHeader(byte[] buffer) throws HeaderReaderException {
        assert buffer != null;
        assert buffer.length >= maxHeaderSize;

        int offset = 0;
        int readByteCount;
        int byteCountToRead = maxHeaderSize;

        while (byteCountToRead > 0) {
            try {
                readByteCount = inputStream.read(buffer, offset, byteCountToRead);
                if (readByteCount < 0) {
                    // data size is fewer then maxHeaderSize
                    break;
                }
            } catch (IOException e) {
                throw new HeaderReaderException(HeaderReaderException.Type.IO);
            }
            byteCountToRead -= readByteCount;
            offset += readByteCount;
        }
        // check was eny data read
        if (offset == 0) {
            throw new HeaderReaderException(HeaderReaderException.Type.FEW_DATA);
        }
        // offset is equal to total read bytes count
        return offset;
    }
}
