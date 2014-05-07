/*
 * Created on Jul 7, 2005
 *
 */
package com.mohaine.util;

import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.Socket;

public class StreamUtils {

    public static String readLine(InputStream inputStream) throws IOException {
        StringBuffer sb = new StringBuffer();
        while (true) {
            int read = inputStream.read();
            if (read < 0) {
                if (sb.length() == 0) {
                    return null;
                }
                break;
            }
            if (read == '\n') {
                break;
            }
            if (read != '\r' && read != '\n') {
                sb.append((char) read);
            }
        }
        return sb.toString();
    }

    public static byte[] readStream(DataInput is, int lengthToRead) throws IOException {
        byte[] buffer = new byte[lengthToRead];
        is.readFully(buffer);
        return buffer;
    }

    public static void bypassStream(InputStream is, int lengthToRead) throws IOException {
        byte[] buffer = new byte[5000];
        int index = 0;
        while (index < lengthToRead) {
            int readLength = Math.min(lengthToRead - index, buffer.length);
            int read = is.read(buffer, 0, readLength);
            if (read < 0) {
                throw new RuntimeException("Unexpected end of stream");
            }
            index += read;
        }
    }

    public static byte[] readStream(InputStream is, int lengthToRead) throws IOException {
        byte[] buffer = new byte[lengthToRead];
        int index = 0;
        while (index < lengthToRead) {
            int read = is.read(buffer, index, lengthToRead - index);
            if (read < 0) {
                throw new RuntimeException("Unexpected end of stream");
            }
            index += read;
        }
        return buffer;
    }

    public static byte[] readStream(InputStream is) throws IOException {
        byte[] toByteArray;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        writeStream(is, byteArrayOutputStream);
        toByteArray = byteArrayOutputStream.toByteArray();
        return toByteArray;
    }

    public static void writeStream(Reader is, Writer os) throws IOException {
        char[] buffer = new char[5000];
        while (true) {
            int readSize = is.read(buffer);
            if (readSize > 0) {
                os.write(buffer, 0, readSize);
            } else if (readSize < 0) {
                break;
            }
        }
    }

    public static void writeStream(InputStream is, OutputStream os) throws IOException {
        byte[] buffer = new byte[5000];
        while (true) {
            int readSize = is.read(buffer);
            if (readSize > 0) {
                os.write(buffer, 0, readSize);
            } else if (readSize < 0) {
                break;
            }
        }
    }

    public static void close(OutputStream os) {
        try {
            if (os != null) {
                os.close();
            }
        } catch (Throwable e) {
            // Ignore
        }
    }

    public static void close(InputStream is) {
        try {
            if (is != null) {
                is.close();
            }
        } catch (Throwable e) {
            // Ignore
        }
    }

    public static void close(Reader reader) {
        try {
            if (reader != null) {
                reader.close();
            }
        } catch (Throwable e) {
            // Ignore
        }
    }

    public static void close(Writer writer) {
        try {
            if (writer != null) {
                writer.close();
            }
        } catch (Throwable e) {
            // Ignore
        }
    }

    public static void close(Socket socket) {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (Throwable e) {
            // Ignore
        }

    }

}
