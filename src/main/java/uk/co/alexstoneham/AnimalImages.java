package uk.co.alexstoneham;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AnimalImages {
    private static final HttpClient client = HttpClient.newHttpClient();

    private static final String DOG_API = "https://dog.ceo/api/breeds/image/random";
    private static final String CAT_API = "https://api.thecatapi.com/v1/images/search";
    private static final String FOX_API = "https://randomfox.ca/floof/";
    private static final String DUCK_API = "https://random-d.uk/api/random";

    public static void main(String[] args) {
        JFrame frame = new JFrame("Random Animal Images");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 500);
        frame.setResizable(false);

        JLabel label = new JLabel();
        label.setHorizontalAlignment(SwingConstants.CENTER);
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.add(label, BorderLayout.CENTER);

        // Buttons
        JButton dogButton = new JButton("🐶 Dog");
        JButton catButton = new JButton("🐱 Cat");
        JButton foxButton = new JButton("🦊 Fox");
        JButton duckButton = new JButton("🦆 Duck");

        for (JButton btn : new JButton[]{dogButton, catButton, foxButton, duckButton}) {
            btn.setFont(new Font("SansSerif", Font.BOLD, 16));
        }

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(dogButton);
        buttonPanel.add(catButton);
        buttonPanel.add(foxButton);
        buttonPanel.add(duckButton);

        frame.setLayout(new BorderLayout());
        frame.add(imagePanel, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);
        frame.setVisible(true);

        // Button handlers to display image
        dogButton.addActionListener((ActionEvent e) -> fetchImage(DOG_API, "message", false, label, frame));
        catButton.addActionListener((ActionEvent e) -> fetchImage(CAT_API, "url", true, label, frame));
        foxButton.addActionListener((ActionEvent e) -> fetchImage(FOX_API, "image", false, label, frame));
        duckButton.addActionListener((ActionEvent e) -> fetchImage(DUCK_API, "url", false, label, frame));
    }

    private static void fetchImage(String apiUrl, String key, boolean isArray, JLabel label, Component parent) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String imageUrl;

            // some APIs eg the cat one have the objects in an array, so cater for this if its the case
            if (isArray) {
                JSONArray array = new JSONArray(response.body());
                imageUrl = array.getJSONObject(0).getString(key);
            } else {
                JSONObject json = new JSONObject(response.body());
                imageUrl = json.getString(key);
            }

            label.setIcon(new ImageIcon(new URL(imageUrl)));
        } catch (IOException | InterruptedException | JSONException ex) {
            showError(parent, ex);
        }
    }

    private static void showError(Component parent, Exception ex) {
        JOptionPane.showMessageDialog(parent, "Failed to load image:\n" + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
    }
}