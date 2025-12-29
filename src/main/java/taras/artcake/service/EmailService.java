package taras.artcake.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.*;

@Service
public class EmailService {

    @Value("${resend.api.key:}")
    private String resendApiKey;

    @Value("${app.mail.from:artcake@artcake.no}")
    private String mailFrom;

    private static final String RESEND_FALLBACK_FROM = "onboarding@resend.dev";
    private static final String ARTCAKE_EMAIL = "artcake@artcake.no";

    private final HttpClient httpClient;

    public EmailService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    @Async
    public void sendOrderEmail(String customerName, String customerEmail, String customerPhone,
                               String deliveryDate, String notes,
                               List<CartService.CartItemDTO> cartItems, BigDecimal total) {
        // Send to konditor
        sendOrderEmailToKonditor(customerName, customerEmail, customerPhone, deliveryDate, notes, cartItems, total);

        // Send confirmation to customer
        sendConfirmationEmailToCustomer(customerName, customerEmail, customerPhone, deliveryDate, notes, cartItems, total);
    }

    private void sendOrderEmailToKonditor(String customerName, String customerEmail, String customerPhone,
                                          String deliveryDate, String notes,
                                          List<CartService.CartItemDTO> cartItems, BigDecimal total) {
        try {
            String subject = "NY BESTILLING! - " + customerName;
            String body = buildOrderEmailBody(customerName, customerEmail, customerPhone, deliveryDate, notes, cartItems, total, true);

            // Collect attachments (custom images)
            List<String> attachmentPaths = new ArrayList<>();
            for (CartService.CartItemDTO item : cartItems) {
                if ("custom".equals(item.itemType) && item.getCustomImageUrl() != null) {
                    String path = resolvePhysicalPath(item.getCustomImageUrl());
                    if (path != null) {
                        attachmentPaths.add(path);
                    }
                }
            }

            sendEmailViaResend("artcake@artcake.no", subject, body, customerEmail, attachmentPaths);

        } catch (Exception e) {
            System.err.println("Feil ved sending av ordre-epost til konditor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void sendConfirmationEmailToCustomer(String customerName, String customerEmail, String customerPhone,
                                                 String deliveryDate, String notes,
                                                 List<CartService.CartItemDTO> cartItems, BigDecimal total) {
        try {
            String subject = "Ordrebekreftelse - ArtCake";
            String body = buildOrderEmailBody(customerName, customerEmail, customerPhone, deliveryDate, notes, cartItems, total, false);

            // Collect attachments (custom images) - also send back to customer as confirmation
            List<String> attachmentPaths = new ArrayList<>();
            for (CartService.CartItemDTO item : cartItems) {
                if ("custom".equals(item.itemType) && item.getCustomImageUrl() != null) {
                    String path = resolvePhysicalPath(item.getCustomImageUrl());
                    if (path != null) {
                        attachmentPaths.add(path);
                    }
                }
            }

            sendEmailViaResend(customerEmail, subject, body, "artcake@artcake.no", attachmentPaths);

        } catch (Exception e) {
            System.err.println("Feil ved sending av bekreftelse til kunde: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Async
    public void sendContactMessage(String name, String email, String message) {
        try {
            String subject = "Ny melding fra kontaktskjema - " + name;
            String body = "Navn: " + name + "\n" +
                    "Epost: " + email + "\n\n" +
                    "Melding:\n" + message;

            sendEmailViaResend("artcake@artcake.no", subject, body, email, null);

        } catch (Exception e) {
            System.err.println("Feil ved sending av kontaktskjema-epost: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void sendEmailViaResend(String to, String subject, String textBody, String replyTo, List<String> attachmentPaths) {
        String apiKey = System.getenv("RESEND_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            apiKey = this.resendApiKey;
        }

        if (apiKey == null || apiKey.isEmpty()) {
            System.err.println("Mangler RESEND_API_KEY. Kan ikke sende epost.");
            return;
        }

        try {
            List<Map<String, Object>> attachments = new ArrayList<>();
            if (attachmentPaths != null) {
                for (String pathStr : attachmentPaths) {
                    Path path = Paths.get(pathStr);
                    if (Files.exists(path)) {
                        try {
                            byte[] content = Files.readAllBytes(path);
                            String base64Content = Base64.getEncoder().encodeToString(content);
                            String filename = path.getFileName().toString();

                            Map<String, Object> attachment = new HashMap<>();
                            attachment.put("filename", filename);
                            attachment.put("content", base64Content);
                            attachments.add(attachment);
                        } catch (IOException e) {
                            System.err.println("Kunne ikke lese vedlegg: " + pathStr + " - " + e.getMessage());
                        }
                    } else {
                        System.err.println("Vedleggsfil finnes ikke: " + pathStr);
                    }
                }
            }

            Map<String, Object> payload = new HashMap<>();

            String fromAddress = (this.mailFrom != null && !this.mailFrom.isEmpty()) ? this.mailFrom : RESEND_FALLBACK_FROM;

            if (!fromAddress.contains("<")) {
                fromAddress = "ArtCake <" + fromAddress + ">";
            }

            payload.put("from", fromAddress);
            payload.put("to", Collections.singletonList(to));
            payload.put("subject", subject);
            payload.put("text", textBody);
            if (replyTo != null) {
                payload.put("reply_to", replyTo);
            }
            if (!attachments.isEmpty()) {
                payload.put("attachments", attachments);
            }

            ObjectMapper mapper = new ObjectMapper();
            String jsonBody = mapper.writeValueAsString(payload);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.resend.com/emails"))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpClient client = this.httpClient;
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 400) {
                System.err.println("Feil fra Resend API (" + response.statusCode() + "): " + response.body());
            } else {
                System.out.println("Epost sendt til " + to + " via Resend. Status: " + response.statusCode());
            }

        } catch (Exception e) {
            System.err.println("Unntak ved sending av epost via Resend: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String buildOrderEmailBody(String customerName, String customerEmail, String customerPhone,
                                        String deliveryDate, String notes,
                                        List<CartService.CartItemDTO> cartItems, BigDecimal total, boolean isNewOrder) {
        StringBuilder body = new StringBuilder();
        body.append("Hei ").append(customerName).append(",\n\n");

        if (isNewOrder) {
            body.append("Takk for din bestilling hos ArtCake! Vi er glade for å kunne lage kaken til deg.\n\n");
        } else {
            body.append("Takk for at du valgte ArtCake igjen! Her er detaljene for din bestilling:\n\n");
        }

        body.append("KUNDEINFO:\n");
        body.append("Navn: ").append(customerName).append("\n");
        body.append("Epost: ").append(customerEmail).append("\n");
        body.append("Telefon: ").append(customerPhone).append("\n");
        body.append("Ønsket leveringsdato: ").append(deliveryDate).append("\n\n");

        body.append("═══════════════════════\n");
        body.append("BESTILLINGSDETAJLER\n");
        body.append("═══════════════════════\n");

        int itemNumber = 1;
        for (CartService.CartItemDTO item : cartItems) {
            body.append(itemNumber).append(". ");
            if ("standard".equals(item.getItemType())) {
                body.append(item.getCakeName()).append(" (").append(item.getSizeCm()).append(" cm)\n");
                body.append("   Antall: ").append(item.getQuantity()).append("\n");
                body.append("   Pris per stk: ").append(item.getPrice()).append(" kr\n");
            } else {
                body.append("Personlig kake\n");
                body.append("   Beskrivelse:\n");
                body.append("   ").append(item.getCustomDescription()).append("\n");
                body.append("   Estimert pris: ").append(item.getPrice()).append(" kr\n");
            }
            itemNumber++;
        }

        body.append("\n═══════════════════════\n");
        body.append("Total: ").append(total).append(" kr\n\n");

        if (notes != null && !notes.isEmpty()) {
            body.append("NOTATER FRA KUNDE:\n");
            body.append("───────────────────────\n");
            body.append(notes).append("\n\n");
        }

        body.append("Vi vil kontakte deg snart for å bekrefte detaljer og gi deg en endelig pris.\n\n");
        body.append("Takk for at du velger ArtCake!\n\n");
        body.append("Med vennlig hilsen,\nArtCake Studio");

        return body.toString();
    }

    private String resolvePhysicalPath(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }

        if (url.startsWith("/custom-image/")) {
            String filename = url.substring("/custom-image/".length());

            // Sjekk om vi er i dev
            if (Files.exists(Paths.get("src", "main", "resources"))) {
                return Paths.get("src", "main", "resources", "static", "images", "custom-uploads", filename).toString();
            } else {
                // Prod
                return Paths.get("/app/uploads", filename).toString();
            }
        }

        if (url.startsWith("/images/custom-uploads/")) {
             String filename = url.substring("/images/custom-uploads/".length());
             if (Files.exists(Paths.get("src", "main", "resources"))) {
                return Paths.get("src", "main", "resources", "static", "images", "custom-uploads", filename).toString();
            }
        }

        return null;
    }
}
