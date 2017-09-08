package jw.ms.server.handler;

import java.nio.channels.SelectionKey;

/**
 * Created by jw on 2017/9/6.
 */
public interface EventHandler {

    void handleEvent(SelectionKey handle) throws Exception;
}
