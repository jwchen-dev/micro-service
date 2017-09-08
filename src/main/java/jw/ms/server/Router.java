package jw.ms.server;

import jw.ms.request.AbstractRequest;

import java.nio.channels.SelectionKey;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by jw on 2017/9/8.
 */
public class Router {

    private static Map<String, AbstractRequest> routerMap = new TreeMap<String, AbstractRequest>();

    public static void get(String path, AbstractRequest request) {
        routerMap.put(path, request);
    }

    public static void doProcess(String path, SelectionKey handle) {
        try {
            routerMap.get(path).handle(handle);
        }catch(Exception e){
            System.out.println(path);
            throw e;
        }
    }
}
