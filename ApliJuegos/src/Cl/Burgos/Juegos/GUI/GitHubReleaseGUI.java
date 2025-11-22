package Cl.Burgos.Juegos.GUI;

import Cl.Burgos.Juegos.FUN.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class GitHubReleaseGUI {

    private static String downloadUrl = "";
    private static String releasePageUrl = "";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GitHubReleaseGUI::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("GitHub Release Viewer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(650, 550);
        frame.setLocationRelativeTo(null);

        JTextField repoField = new JTextField(Confi.repositorio);
        JButton fetchButton = new JButton("Consultar Release");
        JButton downloadButton = new JButton("Descargar archivo");
        JButton openGitHubButton = new JButton("Ver en GitHub");

        downloadButton.setEnabled(false);
        openGitHubButton.setEnabled(false);

        JTextArea outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        outputArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        outputArea.setBackground(new Color(245, 245, 245));
        JScrollPane scrollPane = new JScrollPane(outputArea);

        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setVisible(false);

        // Panel superior
        JPanel repoPanel = new JPanel();
        repoPanel.setLayout(new BoxLayout(repoPanel, BoxLayout.X_AXIS));
        repoPanel.setBorder(BorderFactory.createTitledBorder("Repositorio GitHub"));
        repoPanel.add(repoField);
        repoPanel.add(fetchButton);

        // Panel inferior
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createTitledBorder("Acciones"));
        buttonPanel.add(openGitHubButton);
        buttonPanel.add(downloadButton);

        // Panel central
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createTitledBorder("Información del Release"));
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(progressBar, BorderLayout.SOUTH);

        // Acciones
        fetchButton.addActionListener((ActionEvent e) -> {
            String repoUrl = repoField.getText().trim();
            outputArea.setText("Consultando...\n");
            downloadButton.setEnabled(false);
            openGitHubButton.setEnabled(false);
            downloadUrl = "";
            releasePageUrl = "";
            progressBar.setVisible(true);

            new Thread(() -> {
                String result = fetchReleaseInfo(repoUrl);
                SwingUtilities.invokeLater(() -> {
                    outputArea.setText(result);
                    downloadButton.setEnabled(!downloadUrl.isEmpty());
                    openGitHubButton.setEnabled(!releasePageUrl.isEmpty());
                    progressBar.setVisible(false);
                });
            }).start();
        });

        downloadButton.addActionListener((ActionEvent e) -> {
            try {
                Desktop.getDesktop().browse(new URI(downloadUrl));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "❌ Error al abrir el enlace de descarga.");
            }
        });

        openGitHubButton.addActionListener((ActionEvent e) -> {
            try {
                Desktop.getDesktop().browse(new URI(releasePageUrl));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "❌ Error al abrir la página del release.");
            }
        });

        frame.setLayout(new BorderLayout());
        frame.add(repoPanel, BorderLayout.NORTH);
        frame.add(centerPanel, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    private static String fetchReleaseInfo(String repoUrl) {
        try {
            String[] parts = repoUrl.replace("https://github.com/", "").split("/");
            if (parts.length < 2) return "❌ URL inválida.";

            String owner = parts[0];
            String repo = parts[1];
            String apiUrl = "https://api.github.com/repos/" + owner + "/" + repo + "/releases";

            URI uri = new URI(apiUrl);
            URL url = uri.toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/vnd.github.v3+json");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder json = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }
            reader.close();

            String jsonText = json.toString();
            if (jsonText.startsWith("[]")) return "⚠️ El repositorio no tiene releases publicados.";

            String tag = extractValue(jsonText, "\"tag_name\":\"");
            String description = extractValue(jsonText, "\"body\":\"");
            String date = extractValue(jsonText, "\"published_at\":\"");
            String htmlUrl = extractValue(jsonText, "\"html_url\":\"");

            releasePageUrl = htmlUrl;

            StringBuilder result = new StringBuilder();
            result.append("Repositorio: ").append(repo).append("\n");
            result.append("Versión: ").append(tag.isEmpty() ? "—" : tag).append("\n");
            result.append("Descripción: ").append(description.isEmpty() ? "Sin descripción" : description).append("\n");
            result.append("Fecha: ").append(date.isEmpty() ? "—" : date).append("\n");
            result.append("Release: ").append(htmlUrl.isEmpty() ? "—" : htmlUrl).append("\n");

            int assetBlockStart = jsonText.indexOf("\"assets\":[");
            if (assetBlockStart != -1) {
                String assetName = extractValue(jsonText, "\"name\":\"");
                String assetUrl = extractValue(jsonText, "\"browser_download_url\":\"");
                if (!assetUrl.isEmpty()) {
                    result.append("Archivo disponible: ").append(assetName).append("\n");
                    result.append("Descarga directa: ").append(assetUrl).append("\n");
                    downloadUrl = assetUrl;
                } else {
                    result.append("No hay archivos disponibles para descarga.\n");
                }
            } else {
                result.append("No se encontraron assets en el release.\n");
            }

            return result.toString();

        } catch (Exception e) {
            Log.log("❌ Error: " + e.getMessage());
            return "❌ Error: " + e.getMessage();
        }
    }

    private static String extractValue(String json, String key) {
        int start = json.indexOf(key);
        if (start == -1) return "";
        start += key.length();
        int end = json.indexOf("\"", start);
        return end > start ? json.substring(start, end) : "";
    }
    
    public static boolean hayNuevaVersion(String repoUrl, String versionActual) {
        try {
            String[] parts = repoUrl.replace("https://github.com/", "").split("/");
            if (parts.length < 2) return false;

            String owner = parts[0];
            String repo = parts[1];
            String apiUrl = "https://api.github.com/repos/" + owner + "/" + repo + "/releases/latest";

            URI uri = new URI(apiUrl);
            URL url = uri.toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/vnd.github.v3+json");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder json = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }
            reader.close();

            String tag = extractValue(json.toString(), "\"tag_name\":\"");
            return isNewerVersion(versionActual.replaceFirst("^v", ""), tag.replaceFirst("^v", ""));

        } catch (Exception e) {
            Log.log("❌ Error al verificar versión: " + e.getMessage());
            System.err.println("❌ Error al verificar versión: " + e.getMessage());
            return false;
        }
    }
    private static boolean isNewerVersion(String current, String remote) {
        String[] currentParts = current.split("\\.");
        String[] remoteParts = remote.split("\\.");

        int length = Math.max(currentParts.length, remoteParts.length);
        for (int i = 0; i < length; i++) {
            int c = i < currentParts.length ? Integer.parseInt(currentParts[i]) : 0;
            int r = i < remoteParts.length ? Integer.parseInt(remoteParts[i]) : 0;
            if (r > c) return true;
            if (r < c) return false;
        }
        return false;
    }
    public static String obtenerUltimaVersion(String repoUrl) {
        try {
            String[] parts = repoUrl.replace("https://github.com/", "").split("/");
            if (parts.length < 2) return null;

            String owner = parts[0];
            String repo = parts[1];
            String apiUrl = "https://api.github.com/repos/" + owner + "/" + repo + "/releases/latest";

            URI uri = new URI(apiUrl);
            URL url = uri.toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/vnd.github.v3+json");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder json = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }
            reader.close();

            String tag = extractValue(json.toString(), "\"tag_name\":\"");
            return tag.replaceFirst("^v", "").trim(); // Limpia el prefijo "v" si existe

        } catch (Exception e) {
            Log.log("❌ Error al obtener versión remota: " + e.getMessage());
            System.err.println("❌ Error al obtener versión remota: " + e.getMessage());
            return null;
        }
    }
}
