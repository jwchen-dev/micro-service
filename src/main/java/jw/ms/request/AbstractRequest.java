package jw.ms.request;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;

/**
 * Created by jw on 2017/9/8.
 */
public abstract class AbstractRequest {

    public abstract void handle(SelectionKey handle);
    
}
