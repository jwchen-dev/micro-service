package jw.ms.server;

import jw.ms.request.AbstractRequest;
import jw.ms.server.handler.AcceptEventHandler;
import jw.ms.server.handler.ReadEventHandler;
import jw.ms.server.handler.WriteEventHandler;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;

/**
 * Created by jw on 2017/9/6.
 */
public class Bootstrap implements Runnable{
    private static final int SERVER_PORT = 8080;


    public void startReactor(int port) throws Exception {
        ServerSocketChannel server = ServerSocketChannel.open();
        server.socket().bind(new InetSocketAddress(port),10*1024);
        server.socket().setReuseAddress(true);
        server.configureBlocking(false);

        Reactor reactor = new Reactor();
        reactor.registerChannel(SelectionKey.OP_ACCEPT, server);
//        reactor.registerReadChannel(SelectionKey.OP_READ, server);

        reactor.registerEventHandler(SelectionKey.OP_ACCEPT, new AcceptEventHandler(reactor.getDemultiplexer()));

        reactor.registerEventHandler(SelectionKey.OP_READ, new ReadEventHandler(reactor.getDemultiplexer()));
//        new Thread(new ReadEventHandler(reactor.getReadDemultiplexer())).start();

        reactor.registerEventHandler(SelectionKey.OP_WRITE, new WriteEventHandler());

        reactor.run();
    }

    public static void main(String[] args) {
        System.out.println("Server Started at port : " + SERVER_PORT);
        try {
//            new Bootstrap().startReactor(SERVER_PORT);
            new Thread(new Bootstrap()).start();

            Router.get("/hello",new CustomRequest());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            ServerSocketChannel server = ServerSocketChannel.open();
            server.socket().bind(new InetSocketAddress(SERVER_PORT), 8 * 1024);
            server.socket().setReuseAddress(true);
            server.configureBlocking(false);

            Reactor reactor = new Reactor();
            reactor.registerChannel(SelectionKey.OP_ACCEPT, server);
//        reactor.registerReadChannel(SelectionKey.OP_READ, server);

            reactor.registerEventHandler(SelectionKey.OP_ACCEPT, new AcceptEventHandler(reactor.getDemultiplexer()));

            reactor.registerEventHandler(SelectionKey.OP_READ, new ReadEventHandler(reactor.getDemultiplexer()));
//        new Thread(new ReadEventHandler(reactor.getReadDemultiplexer())).start();

            reactor.registerEventHandler(SelectionKey.OP_WRITE, new WriteEventHandler());

            reactor.run();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
