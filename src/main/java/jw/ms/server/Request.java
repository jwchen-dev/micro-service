package jw.ms.server;

import java.nio.Buffer;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by jw on 2017/9/8.
 */
public class Request {

    private Map<String, String> map = new TreeMap<String, String>();

    private String method;

    private String path;

    private String version;

    private Buffer buffer;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setHeader(String key, String value) {
        map.put(key, value);
    }

    public String getHeader(String key) {
        return map.get(key);
    }

    public void setResponse(Buffer response){
        this.buffer=response;
    }

    public Buffer getResponse(){
        return this.buffer;
    }

    public Iterator<String> iterator() {
        return map.keySet().iterator();
    }
}
