package jw.ms.server.handler;

import jw.ms.server.Request;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.StringTokenizer;

/**
 * Created by jw on 2017/9/6.
 */
public class ReadEventHandler implements EventHandler, Runnable {

    private Selector demultiplexer;

    private static final byte[] GET = "GET".getBytes();

    private static final byte[] POST = "POST".getBytes();

    private static final byte[] CONTENT_LENGTH = "Content-Length".getBytes();
    private static final char[] CONTENT_LENGTH_CHAR = "Content-Length".toCharArray();

//    private ByteBuffer inputBuffer = ByteBuffer.allocate(2 * 1024);

    private SelectionKey handle;

    public ReadEventHandler(Selector demultiplexer) {
        this.demultiplexer = demultiplexer;
    }

    @Override
    public void handleEvent(SelectionKey handle) throws Exception {
//        System.out.println("===== Read Event Handler =====");
        ByteBuffer inputBuffer = ByteBuffer.allocate(2 * 1024);
//        ByteBuffer inputBuffer = ByteBuffer.allocate(262144);

        SocketChannel socketChannel = (SocketChannel) handle.channel();
//        inputBuffer.clear();
//        socketChannel.read(inputBuffer); // Read data from client

//        inputBuffer.flip();
        // Rewind the buffer to start reading from the beginning

//        byte[] buffer = new byte[inputBuffer.limit()];
//        inputBuffer.get(buffer);

        char[] buffer = read(socketChannel, inputBuffer);

        Request request = processHeader(buffer);
//        System.out.println("Received message from client : " + new String(buffer));
//        inputBuffer.flip();
        // Rewind the buffer to start reading from the beginning
        // Register the interest for writable readiness event for
        // this channel in order to echo back the message

//        socketChannel.register(demultiplexer, SelectionKey.OP_WRITE, inputBuffer);
        socketChannel.register(demultiplexer, SelectionKey.OP_WRITE, request);
    }

    //TODO use ByteBuffer process data, char or string if need.
    private char[] read(SocketChannel socketChannel, ByteBuffer byteBuffer) throws Exception {
        int bytesRead = socketChannel.read(byteBuffer);
        char[] buff = new char[byteBuffer.limit()];
        int offset = 0;
        boolean isPost = false;
        int contentLength = -1;

        //check request method
        if (bytesRead > 0) {
            byteBuffer.flip();

            while (byteBuffer.hasRemaining()) {
                buff[offset++] = (char) byteBuffer.get();
            }

            byteBuffer.clear();

            bytesRead = socketChannel.read(byteBuffer);

            if (bytesRead == 0) {
                if (buff[0] == 'P') {
                    isPost = true;
                    int lastIdx = match(buff, CONTENT_LENGTH_CHAR);

                    contentLength = matchValue(buff, CONTENT_LENGTH_CHAR);
                }
            }

            //expand
            if ((offset + bytesRead) >= buff.length) {
                buff = Arrays.copyOf(buff, buff.length * 2);
            }
        }

        if (isPost) {
            while (!validateBodyLen(buff, contentLength)) {
                byteBuffer.flip();

                while (byteBuffer.hasRemaining()) {
                    buff[offset++] = (char) byteBuffer.get();
                }

                byteBuffer.clear();

                bytesRead = socketChannel.read(byteBuffer);


                //expand
                if ((offset + bytesRead) >= buff.length) {
                    buff = Arrays.copyOf(buff, buff.length * 2);
                }
            }
        }

        byteBuffer.clear();

        return buff;
    }

    /**
     * @param buff
     * @param match
     * @return last word index
     */
    private int match(char[] buff, char[] match) {
        for (int i = 0; i < buff.length; i++) {
            next:

            //hint first word
            if (buff[i] == match[0]) {
                for (int x = 1; x < match.length; x++) {
                    if (buff[++i] != match[x]) {
                        break next;
                    }
                }

                return i;
            }
        }

        return -1;
    }

    private int matchValue(char[] buff, char[] match) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < buff.length; i++) {
            next:

            //hint first word
            if (buff[i] == match[0]) {
                for (int x = 1; x < match.length; x++) {
                    if (buff[++i] != match[x]) {
                        break next;
                    }
                }

                for (int j = i; j < buff.length; j++) {
                    if (buff[j] >= 48 && buff[j] <= 57) {
                        builder.append(buff[j]);
                    }

                    if (buff[j] == '\r') {
                        return Integer.parseInt(builder.toString());
                    }
                }
            }
        }

        return -1;
    }

    private boolean validateBodyLen(char[] buff, int expectLen) {
        int lastIdx = match(buff, "\r\n\r\n".toCharArray());

        for (int i = lastIdx; i < buff.length; i++) {
            if (buff[i] == '\u0000') {
                if (i - (lastIdx + 1) == expectLen) {
                    return true;
                }
                break;
            }
        }

        return false;
    }

    private Request processHeader(char[] buffer) {
        Request request = new Request();

        StringBuilder stringBuilder = new StringBuilder();
        System.out.println(new String(buffer));
        for (int i = 0, size = buffer.length; i < size; i++) {
            if (buffer[i] == '\n') {
                String line = stringBuilder.toString();
                //TODO need?
                line = line.trim();
                if (line.length() > 0) {
                    StringTokenizer stringTokenizer = new StringTokenizer(line);
                    String name = stringTokenizer.nextToken();

                    switch (name) {
                        // sample: GET / HTTP/1.0
                        case "GET":
                        case "POST":
                            String[] vals = new String[2];
                            vals[0] = stringTokenizer.nextToken();
                            vals[1] = stringTokenizer.nextToken();
                            request.setMethod(name);
                            request.setPath(vals[0]);
                            request.setVersion(vals[1]);
                            break;
                        default:
                            request.setHeader(name, stringTokenizer.nextToken());
//                            System.out.println(name+"\t"+request.getHeader(name));
                            break;
                    }
                }
                stringBuilder = new StringBuilder();
            } else {
                stringBuilder.append(buffer[i]);
            }
        }

//        map.put("raw", new String(buffer));

        return request;
    }

    @Override
    public void run() {
//        try {
//            //        System.out.println("===== Read Event Handler =====");
////        ByteBuffer inputBuffer = ByteBuffer.allocate(2 * 1024);
//
//            SocketChannel socketChannel = (SocketChannel) this.handle.channel();
//            inputBuffer.clear();
//            socketChannel.read(inputBuffer); // Read data from client
//
//            inputBuffer.flip();
//            // Rewind the buffer to start reading from the beginning
//
//            byte[] buffer = new byte[inputBuffer.limit()];
//            inputBuffer.get(buffer);
//
////        System.out.println("Received message from client : " + new String(buffer));
//            inputBuffer.flip();
//            // Rewind the buffer to start reading from the beginning
//            // Register the interest for writable readiness event for
//            // this channel in order to echo back the message
//
//            socketChannel.register(this.demultiplexer, SelectionKey.OP_WRITE, inputBuffer);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public void setHandle(SelectionKey handle) {
        this.handle = handle;
    }
}
