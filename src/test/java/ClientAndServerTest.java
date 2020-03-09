import org.junit.Test;

import java.io.IOException;

public class ClientAndServerTest {

    @Test
    public void testSendAndReceiveText() throws IOException, InterruptedException {
        (new Thread(() -> {
            try {
                new Server(7777);
            } catch (IOException e) {
                e.printStackTrace();
            }
        })).start();
        Thread.sleep(100);
        for (int i = 0; i < 1000; i++) {
            (new Thread(() -> {
                try {
                    Client client = new Client("localhost", 7777);
                    client.sendTextRequest();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            })).start();
        }
    }
}
