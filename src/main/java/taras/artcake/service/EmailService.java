package taras.artcake.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Value("${resend.api.key:}")
    private String resendApiKey;

    @Value("${app.mail.from:artcake@artcake.no}")
    private String mailFrom;

    // Resend krever verifisert domene. Bruk denne hvis artcake.no ikke er verifisert enda.
    private static final String RESEND_FALLBACK_FROM = "onboarding@resend.dev";
    private static final String ARTCAKE_EMAIL = "artcake@artcake.no";

    private final HttpClient httpClient;

    public EmailService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    public void sendOrderEmail(String customerName, String customerEmail, String customerPhone,
                               String deliveryDate, String notes,
                               List<CartService.CartItemDTO> cartItems, BigDecimal cartTotal) {

        CompletableFuture.runAsync(() -> {
            try {
                logger.info("=== SENDING ORDER EMAIL VIA RESEND (Background) ===");

                // 1. Send til Konditor (ArtCake)
                // Svar-til settes til kunden, slik at konditor kan svare direkte
                String konditorSubject = "Ny bestilling fra " + customerName;
                String konditorBody = buildOrderEmailContent(customerName, customerEmail, customerPhone,
                        deliveryDate, notes, cartItems, cartTotal);

                sendEmailViaResend(ARTCAKE_EMAIL, konditorSubject, konditorBody, customerEmail);

                // 2. Send bekreftelse til Kunde
                // Svar-til settes til ArtCake
                String customerSubject = "Bestillingen din er mottatt - ArtCake AS";
                String customerBody = buildConfirmationEmailContent(customerName, cartItems, cartTotal);

                sendEmailViaResend(customerEmail, customerSubject, customerBody, ARTCAKE_EMAIL);

            } catch (Exception e) {
                logger.error("Critical error in email background task", e);
            }
        });
    }

    public void sendContactMessage(String senderName, String senderEmail, String body) {
        CompletableFuture.runAsync(() -> {
            try {
                // Til ArtCake (Svar-til: avsender)
                String subject = "Ny kontaktskjema-melding fra " + senderName;
                StringBuilder sb = new StringBuilder();
                sb.append("Melding fra kontaktskjema:\n\n");
                sb.append("Navn: ").append(senderName).append("\n");
                sb.append("Epost: ").append(senderEmail).append("\n\n");
                sb.append(body).append("\n");

                sendEmailViaResend(ARTCAKE_EMAIL, subject, sb.toString(), senderEmail);

                // Bekreftelse til avsender (Svar-til: ArtCake)
                String confSubject = "Takk for din melding til ArtCake";
                String confBody = "Hei " + senderName + "\n\nTakk for meldingen! Vi vil kontakte deg snart.\n\nMed vennlig hilsen,\nArtCake AS";

                sendEmailViaResend(senderEmail, confSubject, confBody, ARTCAKE_EMAIL);

            } catch (Exception e) {
                logger.error("Error sending contact form emails", e);
            }
        });
    }

    // Hjelpemetode for å sende via Resend API med Reply-To støtte
    private void sendEmailViaResend(String to, String subject, String textContent, String replyTo) {
        if (resendApiKey == null || resendApiKey.isEmpty()) {
            logger.error("RESEND_API_KEY mangler! Kan ikke sende e-post.");
            return;
        }

        try {
            // Bruker alltid onboarding-adressen som avsender hvis domenet ikke matcher
            // Men siden du har verifisert artcake.no, vil mailFrom (artcake@artcake.no) fungere.
            String fromAddress = mailFrom.contains("artcake.no") ? mailFrom : RESEND_FALLBACK_FROM;

            String safeSubject = escapeJson(subject);
            String safeText = escapeJson(textContent);
            String safeTo = escapeJson(to);
            String safeFrom = escapeJson(fromAddress);
            String safeReplyTo = escapeJson(replyTo);

            // JSON med reply_to feltet
            String jsonBody = String.format(
                    "{\"from\": \"%s\", \"to\": [\"%s\"], \"subject\": \"%s\", \"text\": \"%s\", \"reply_to\": \"%s\"}",
                    safeFrom, safeTo, safeSubject, safeText, safeReplyTo
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.resend.com/emails"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + resendApiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200 || response.statusCode() == 201) {
                logger.info("✓ E-post sendt via Resend til: {} (Reply-To: {})", to, replyTo);
            } else {
                logger.error("✗ Feil fra Resend ({}): {}", response.statusCode(), response.body());
            }

        } catch (Exception e) {
            logger.error("Unntak ved sending til Resend: {}", e.getMessage());
        }
    }

    private String escapeJson(String input) {
        if (input == null) return "";
        return input.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "");
    }

    private String buildOrderEmailContent(String customerName, String customerEmail,
                                          String customerPhone, String deliveryDate,
                                          String notes, List<CartService.CartItemDTO> cartItems,
                                          BigDecimal cartTotal) {
        StringBuilder content = new StringBuilder();
        content.append("═══════════════════════════════════════\n");
        content.append("NY BESTILLING!\n");
        content.append("═══════════════════════════════════════\n\n");

        content.append("KUNDEINFO:\n");
        content.append("─────────────────────────────────────────\n");
        content.append("Navn: ").append(customerName).append("\n");
        content.append("Epost: ").append(customerEmail).append("\n");
        content.append("Telefon: ").append(customerPhone).append("\n");
        content.append("Ønsket leveringsdato: ").append(deliveryDate).append("\n\n");

        content.append("BESTILLING:\n");
        content.append("─────────────────────────────────────────\n");

        int itemNumber = 1;
        for (CartService.CartItemDTO item : cartItems) {
            content.append(itemNumber).append(". ");
            if ("standard".equals(item.getItemType())) {
                content.append(item.getCakeName()).append(" (").append(item.getSizeCm()).append(" cm)\n");
                content.append("   Pris: ").append(item.getPrice()).append(" kr\n\n");
            } else {
                content.append("PERSONLIG KAKE\n");
                content.append("   Beskrivelse: ").append(item.getCustomDescription()).append("\n");
                if (item.getCustomImageUrl() != null && !item.getCustomImageUrl().isEmpty()) {
                    content.append("   Bilde-URL: ").append(item.getCustomImageUrl()).append("\n");
                    content.append("   (Vedlegg støttes ikke i denne versjonen av e-postsystemet, se URL)\n");
                }
                content.append("   Estimert pris: ").append(item.getPrice()).append(" kr\n\n");
            }
            itemNumber++;
        }

        content.append("─────────────────────────────────────────\n");
        content.append("TOTAL: ").append(cartTotal).append(" kr\n\n");

        if (notes != null && !notes.isEmpty()) {
            content.append("NOTATER:\n");
            content.append("─────────────────────────────────────────\n");
            content.append(notes).append("\n\n");
        }

        content.append("═══════════════════════════════════════\n");
        content.append("Ta kontakt med kunden på: ").append(customerPhone).append("\n");
        content.append("eller ").append(customerEmail).append("\n");
        content.append("═══════════════════════════════════════\n");

        return content.toString();
    }

    private String buildConfirmationEmailContent(String customerName,
                                                 List<CartService.CartItemDTO> cartItems,
                                                 BigDecimal cartTotal) {
        StringBuilder content = new StringBuilder();
        content.append("Hei ").append(customerName).append("!\n\n");
        content.append("Takk for din bestilling hos ArtCake AS.\n\n");

        content.append("Vi har mottatt din bestilling med følgende detaljer:\n");
        content.append("═══════════════════════════════════════\n\n");

        int itemNumber = 1;
        for (CartService.CartItemDTO item : cartItems) {
            content.append(itemNumber).append(". ");
            if ("standard".equals(item.getItemType())) {
                content.append(item.getCakeName()).append(" (").append(item.getSizeCm()).append(" cm)\n");
                content.append("   Pris: ").append(item.getPrice()).append(" kr\n");
            } else {
                content.append("Personlig kake\n");
                content.append("   ").append(item.getCustomDescription()).append("\n");
                content.append("   Estimert pris: ").append(item.getPrice()).append(" kr\n");
            }
            itemNumber++;
        }

        content.append("\n═══════════════════════════════════════\n");
        content.append("Total: ").append(cartTotal).append(" kr\n\n");

        content.append("En av våre kakemestere vil kontakte deg snart for å bekrefte detaljer og gi deg en endelig pris.\n\n");
        content.append("Takk for at du velger ArtCake!\n\n");
        content.append("Med vennlig hilsen,\nArtCake AS");
        return content.toString();
    }
}
