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

//    private ByteBuffer inputBuffer = ByteBuffer.allocate(2 * 1024);

    private SelectionKey handle;

    public ReadEventHandler(Selector demultiplexer) {
        this.demultiplexer = demultiplexer;
    }

    @Override
    public void handleEvent(SelectionKey handle) throws Exception {
//        System.out.println("===== Read Event Handler =====");
        ByteBuffer inputBuffer = ByteBuffer.allocate(2 * 1024);

        SocketChannel socketChannel = (SocketChannel) handle.channel();
//        inputBuffer.clear();
//        socketChannel.read(inputBuffer); // Read data from client

//        inputBuffer.flip();
        // Rewind the buffer to start reading from the beginning

//        byte[] buffer = new byte[inputBuffer.limit()];
//        inputBuffer.get(buffer);

char[] buffer=readRequest(socketChannel,inputBuffer);

        Request request = processHeader(buffer);
//        System.out.println("Received message from client : " + new String(buffer));
//        inputBuffer.flip();
        // Rewind the buffer to start reading from the beginning
        // Register the interest for writable readiness event for
        // this channel in order to echo back the message

//        socketChannel.register(demultiplexer, SelectionKey.OP_WRITE, inputBuffer);
        socketChannel.register(demultiplexer, SelectionKey.OP_WRITE, request);
    }

    private char[] readRequest(SocketChannel socketChannel, ByteBuffer byteBuffer) throws Exception {
        int bytesRead = socketChannel.read(byteBuffer);
        char[] buff = new char[byteBuffer.limit()];
        int offset = 0;

        while (bytesRead > 0) {
            byteBuffer.flip();

            while (byteBuffer.hasRemaining()) {
                buff[offset++] = (char) byteBuffer.get();
            }

            byteBuffer.clear();

            bytesRead = socketChannel.read(byteBuffer);

            if ((offset + bytesRead) >= buff.length) {
                buff = Arrays.copyOf(buff, buff.length * 2);
            }
        }

        byteBuffer.clear();

        return buff;
    }

    private Request processHeader(char[] buffer) {
        Request request = new Request();

        StringBuilder stringBuilder = new StringBuilder();
//if(buffer.length!=87){
//    System.out.println("["+buffer.length+", "+bb.limit()+"]"+new String(buffer));
//}
        for (int i = 0, size = buffer.length; i < size; i++) {
            if (buffer[i] == '\n') {
                String line = stringBuilder.toString();
                line = line.trim();
                if (line.length() > 0) {
                    StringTokenizer stringTokenizer = new StringTokenizer(line);
                    String name = stringTokenizer.nextToken();

                    switch (name) {
                        // sample: GET / HTTP/1.0
                        case "GET":
                            String[] vals = new String[2];
                            vals[0] = stringTokenizer.nextToken();
                            vals[1] = stringTokenizer.nextToken();
                            request.setMethod("GET");
                            request.setPath(vals[0]);
                            request.setVersion(vals[1]);
                            break;
                        default:
                            request.setHeader(name, stringTokenizer.nextToken());
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
