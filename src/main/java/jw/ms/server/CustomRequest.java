package jw.ms.server;

import jw.ms.request.AbstractRequest;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;

/**
 * Created by jw on 2017/9/8.
 */
public class CustomRequest extends AbstractRequest {

    @Override
    public void handle(SelectionKey handle) {
        handle.attach(ByteBuffer.wrap(Long.toString(System.currentTimeMillis()).getBytes()));
    }
}
