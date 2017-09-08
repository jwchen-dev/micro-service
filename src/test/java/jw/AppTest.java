package jw;

import junit.framework.TestCase;
import jw.ms.server.Bootstrap;
import jw.ms.server.CustomRequest;
import jw.ms.server.Router;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {

    @Before
    public void setUp() {
        new Thread(new Bootstrap()).start();

        Router.get("/hello", new CustomRequest());
    }

    @Test
    public void test() throws Exception {
        URL url = new URL("http://127.0.0.1:8080/hello2");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        int responseCode = conn.getResponseCode();

        BufferedReader in = new BufferedReader(
                new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        try {
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
        } finally {
            if (in != null) {
                in.close();
            }
        }

        System.out.println(responseCode+"\t"+response.toString());
    }
}
