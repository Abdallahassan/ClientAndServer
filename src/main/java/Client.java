import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class Client {

    private final static int MAX_RETRIALS = 10;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public static void main(String[] args) throws IOException {
        System.out.println(new Client(args[0], Integer.parseInt(args[1])).sendTextRequest());
    }

    public Client(String ip, int port) throws IOException {
        int retrials = 0;
        while (true) {
            try {
                tryConnect(ip, port);
                break;
            } catch (IOException e) {
                if (++retrials == MAX_RETRIALS) {
                    shutDown();
                    throw new IOException("Could not connect to IP " + ip + " port " + port);
                } else {
                    e.printStackTrace();
                }
            }
        }
    }

    private void tryConnect(String ip, int port) throws IOException {
        clientSocket = new Socket(ip, port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public void shutDown() throws IOException {
        if (clientSocket != null) {
            clientSocket.close();
        }
        if (out != null) {
            out.close();
        }
        if (in != null) {
            in.close();
        }
    }

    public Map<String, Integer> sendTextRequest() throws IOException {
        out.println("text");
        out.flush();
        Map<String, Integer> wordCount = new HashMap<>();
        for (String s: in.readLine().split("[\\s,\\.]+")) {
            Integer prev = wordCount.get(s);
            if (prev == null) {
                prev = 0;
            }
            wordCount.put(s, ++prev);
        }
        return wordCount.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).limit(10)
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue, (e1,e2) -> null, HashMap::new));
    }
}
