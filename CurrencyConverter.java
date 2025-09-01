import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import com.google.gson.*;

public class CurrencyConverter {

    static final String API_KEY = "c8349b68feb17f16c2c575fa";  // Replace with your API key

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter amount: ");
        double amount = sc.nextDouble();

        System.out.print("Enter base currency code (e.g., USD): ");
        String base = sc.next().toUpperCase();

        System.out.print("Enter target currency code (e.g., INR): ");
        String target = sc.next().toUpperCase();

        try {
            double rate = getExchangeRate(base, target);
            double convertedAmount = amount * rate;
            System.out.printf("%.2f %s = %.2f %s\n", amount, base, convertedAmount, target);
        } catch (Exception e) {
            System.out.println("Error fetching exchange rate: " + e.getMessage());
        }

        sc.close();
    }

    // Fetch real-time exchange rate from ExchangeRate-API
    public static double getExchangeRate(String base, String target) throws Exception {
        String urlStr = String.format("https://v6.exchangerate-api.com/v6/%s/latest/%s", API_KEY, base);
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        JsonObject json = JsonParser.parseString(response.toString()).getAsJsonObject();

        if (!json.get("result").getAsString().equals("success")) {
            throw new Exception("API error or invalid currency code.");
        }

        JsonObject rates = json.getAsJsonObject("conversion_rates");
        if (!rates.has(target)) {
            throw new Exception("Target currency not supported.");
        }
        return rates.get(target).getAsDouble();
    }
}
