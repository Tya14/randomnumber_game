import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.stream.Collectors;

public class LeaderboardServer {

    // Store leaderboard data in memory (Map)
    static Map<String, Integer> leaderboard = new TreeMap<>(Collections.reverseOrder());

    public static void main(String[] args) throws Exception {
        // Create the server on port 8000
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/leaderboard", new LeaderboardHandler());
        server.createContext("/submitScore", new SubmitScoreHandler());
        
        System.out.println("Leaderboard Server is running on port 8000...");
        server.start();
    }

    // Handler to show leaderboard
    static class LeaderboardHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = getLeaderboard();
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }

        private String getLeaderboard() {
            StringBuilder sb = new StringBuilder();
            sb.append("Leaderboard:\n");
            
            // Display top 5 players (can modify for more or less)
            int rank = 1;
            for (Map.Entry<String, Integer> entry : leaderboard.entrySet()) {
                sb.append("Rank " + rank++ + ": " + entry.getKey() + " - " + entry.getValue() + " points\n");
                if (rank > 5) break;  // Display top 5 only
            }
            return sb.toString();
        }
    }

    // Handler to submit player scores
    static class SubmitScoreHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Get the POST data (username and score)
            InputStreamReader reader = new InputStreamReader(exchange.getRequestBody());
            BufferedReader br = new BufferedReader(reader);
            String data = br.readLine();  // Expecting data in format: username=player1&score=100

            // Extract username and score
            String[] params = data.split("&");
            String username = params[0].split("=")[1];
            int score = Integer.parseInt(params[1].split("=")[1]);

            // Save the score to the leaderboard
            leaderboard.put(username, score);

            // Send the response back
            String response = "Score submitted successfully!";
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}
