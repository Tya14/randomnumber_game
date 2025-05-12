import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;
import java.util.Scanner;

public class Game {

    public static void main(String[] args) {
        // Create a scanner for user input
        Scanner scanner = new Scanner(System.in);

        // Start the game
        System.out.println("Welcome to the Number Guessing Game!");

        // Generate a random number between 1 and 100
        Random random = new Random();
        int numberToGuess = random.nextInt(100) + 1;

        int attempts = 0;
        int guessedNumber = 0;

        // Loop until the user guesses the correct number
        while (guessedNumber != numberToGuess) {
            System.out.print("Enter your guess (1-100): ");
            guessedNumber = scanner.nextInt();
            attempts++;

            // Give feedback to the player
            if (guessedNumber < numberToGuess) {
                System.out.println("Too low! Try again.");
            } else if (guessedNumber > numberToGuess) {
                System.out.println("Too high! Try again.");
            } else {
                System.out.println("Correct! You guessed the number in " + attempts + " attempts.");
            }
        }

        // Ask for the player's name to submit the score
        System.out.print("Enter your name: ");
        String username = scanner.next();

        // Submit the score to the leaderboard server
        submitScore(username, attempts);

        // Close the scanner
        scanner.close();
    }

    // Function to submit the score to the leaderboard server
    public static void submitScore(String username, int score) {
        try {
            // URL of the leaderboard server
            URL url = new URL("http://localhost:8000/submitScore");

            // Open connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            // Set the content type (important for sending form data)
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            // Prepare the POST data
            String postData = "username=" + username + "&score=" + score;

            // Send the data to the server
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = postData.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Get response from server
            int responseCode = connection.getResponseCode();
            System.out.println("Server Response Code: " + responseCode);

            // Read and print the server's response
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            System.out.println("Server Response: " + response.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
