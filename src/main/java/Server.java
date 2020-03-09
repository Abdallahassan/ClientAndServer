import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class Server {

    private final String fileContent;
    private final static int MAX_RETRIALS = 10;
    private ServerSocket serverSocket;

    private class ClientHandler extends Thread {
        private final Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;

        ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        public void run() {
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                while (handleRequest());
                close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private boolean handleRequest() {
            try {
                String line = in.readLine();
                if (line != null && line.equals("text")) {
                    out.println(fileContent);
                    out.flush();
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        private void close() throws IOException {
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
    }

    public static void main(String[] args) throws IOException {
        new Server(Integer.parseInt(args[0]));
    }

    /**
     * Create a new server
     * @param port the port number to connect to
     * @throws IOException In the case of connection failure
     */
    public Server(int port) throws IOException {
        int retrials = 0;
        while (true) {
            try {
                serverSocket = new ServerSocket(port);
                break;
            } catch (IOException e) {
                if (++retrials == MAX_RETRIALS) {
                    throw new IOException("Could not connect to port " + port);
                } else {
                    e.printStackTrace();
                }
            }
        }
        fileContent = readFile(Server.class.getResourceAsStream("islands_in_the_stream.txt"));
        while (true) {
            new ClientHandler(serverSocket.accept()).start();
        }
    }

    private static String readFile(InputStream stream) {
        return new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).lines()
                .collect(Collectors.joining(System.lineSeparator())).replace('\n', ' ');
    }
}
