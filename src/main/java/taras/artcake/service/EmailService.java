package taras.artcake.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

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

    public void sendOrderEmail(String customerName, String customerEmail, String customerPhone,
                               String deliveryDate, String notes,
                               List<CartService.CartItemDTO> cartItems, BigDecimal cartTotal) {

        CompletableFuture.runAsync(() -> {
            try {
                logger.info("=== SENDING ORDER EMAIL VIA RESEND (Background) ===");

                // Samle opp vedlegg (Bilde-URLer)
                List<String> attachments = new ArrayList<>();
                for (CartService.CartItemDTO item : cartItems) {
                    if (item.getCustomImageUrl() != null && !item.getCustomImageUrl().isEmpty()) {
                        attachments.add(item.getCustomImageUrl());
                    }
                }

                // 1. Send til Konditor (ArtCake) - MED VEDLEGG
                String konditorSubject = "Ny bestilling fra " + customerName;
                String konditorBody = buildOrderEmailContent(customerName, customerEmail, customerPhone,
                        deliveryDate, notes, cartItems, cartTotal);

                sendEmailViaResend(ARTCAKE_EMAIL, konditorSubject, konditorBody, customerEmail, attachments);

                // 2. Send bekreftelse til Kunde - MED VEDLEGG (så kunden ser hva de har sendt)
                String customerSubject = "Bestillingen din er mottatt - ArtCake AS";
                String customerBody = buildConfirmationEmailContent(customerName, cartItems, cartTotal);

                sendEmailViaResend(customerEmail, customerSubject, customerBody, ARTCAKE_EMAIL, attachments);

            } catch (Exception e) {
                logger.error("Critical error in email background task", e);
            }
        });
    }

    public void sendContactMessage(String senderName, String senderEmail, String body) {
        CompletableFuture.runAsync(() -> {
            try {
                // Til ArtCake
                String subject = "Ny kontaktskjema-melding fra " + senderName;
                StringBuilder sb = new StringBuilder();
                sb.append("Melding fra kontaktskjema:\n\n");
                sb.append("Navn: ").append(senderName).append("\n");
                sb.append("Epost: ").append(senderEmail).append("\n\n");
                sb.append(body).append("\n");

                sendEmailViaResend(ARTCAKE_EMAIL, subject, sb.toString(), senderEmail, Collections.emptyList());

                // Bekreftelse til avsender
                String confSubject = "Takk for din melding til ArtCake";
                String confBody = "Hei " + senderName + "\n\nTakk for meldingen! Vi vil kontakte deg snart.\n\nMed vennlig hilsen,\nArtCake AS";

                sendEmailViaResend(senderEmail, confSubject, confBody, ARTCAKE_EMAIL, Collections.emptyList());

            } catch (Exception e) {
                logger.error("Error sending contact form emails", e);
            }
        });
    }

    // Oppdatert metode som støtter vedlegg
    private void sendEmailViaResend(String to, String subject, String textContent, String replyTo, List<String> attachmentUrls) {
        if (resendApiKey == null || resendApiKey.isEmpty()) {
            logger.error("RESEND_API_KEY mangler! Kan ikke sende e-post.");
            return;
        }

        try {
            String fromAddress = mailFrom.contains("artcake.no") ? mailFrom : RESEND_FALLBACK_FROM;

            String safeSubject = escapeJson(subject);
            String safeText = escapeJson(textContent);
            String safeTo = escapeJson(to);
            String safeFrom = escapeJson(fromAddress);
            String safeReplyTo = escapeJson(replyTo);

            // Bygg JSON for vedlegg hvis det finnes
            StringBuilder attachmentsJson = new StringBuilder();
            if (attachmentUrls != null && !attachmentUrls.isEmpty()) {
                attachmentsJson.append(", \"attachments\": [");
                for (int i = 0; i < attachmentUrls.size(); i++) {
                    String attachment = attachmentUrls.get(i);

                    // Enkel sjekk: Er det en URL?
                    if (attachment.startsWith("http://") || attachment.startsWith("https://")) {
                        attachmentsJson.append(String.format("{\"path\": \"%s\"}", attachment));
                    } else {
                        // Antar det er en filsti på serveren (Railway)
                        // Prøver å lese filen og sende innholdet
                        try {
                            java.nio.file.Path path = java.nio.file.Paths.get(attachment);

                            // Forsøk å finne filen mer robust
                            if (!java.nio.file.Files.exists(path)) {
                                String relativePath = attachment.startsWith("/") ? attachment.substring(1) : attachment;

                                // Håndter den nye /custom-image/ ruten
                                if (attachment.startsWith("/custom-image/")) {
                                    String filename = attachment.substring("/custom-image/".length());
                                    // Sjekk lokalt vs prod (samme logikk som ImageController)
                                    boolean isLocalDev = java.nio.file.Files.exists(java.nio.file.Paths.get("src", "main", "resources"));
                                    if (isLocalDev) {
                                        path = java.nio.file.Paths.get("src/main/resources/static/images/custom-uploads/").resolve(filename);
                                    } else {
                                        // Sjekk persistent volume først
                                        java.nio.file.Path volumePath = java.nio.file.Paths.get("/app/uploads").resolve(filename);
                                        if (java.nio.file.Files.exists(volumePath)) {
                                            path = volumePath;
                                        } else {
                                            // Fallback til temp
                                            path = java.nio.file.Paths.get(System.getProperty("java.io.tmpdir"), "artcake-uploads").resolve(filename);
                                        }
                                    }
                                }
                                // Ellers prøv standard logikk
                                else {
                                    // 1. Sjekk relativt til working dir (f.eks. "uploads/...")
                                    java.nio.file.Path relativePathObj = java.nio.file.Paths.get(relativePath);
                                    if (java.nio.file.Files.exists(relativePathObj)) {
                                        path = relativePathObj;
                                    }
                                    // 2. Sjekk i target/classes/static (vanlig Spring Boot struktur etter build)
                                    else {
                                        java.nio.file.Path targetPath = java.nio.file.Paths.get("target", "classes", "static", relativePath);
                                        if (java.nio.file.Files.exists(targetPath)) {
                                            path = targetPath;
                                        }
                                        // 3. Sjekk src/main/resources/static (lokal utvikling)
                                        else {
                                            java.nio.file.Path srcPath = java.nio.file.Paths.get("src", "main", "resources", "static", relativePath);
                                            if (java.nio.file.Files.exists(srcPath)) {
                                                path = srcPath;
                                            }
                                        }
                                    }
                                }

                                if (java.nio.file.Files.exists(path)) {
                                    byte[] fileBytes = java.nio.file.Files.readAllBytes(path);
                                    String base64Content = java.util.Base64.getEncoder().encodeToString(fileBytes);
                                    String filename = path.getFileName().toString();

                                    attachmentsJson.append(String.format("{\"content\": \"%s\", \"filename\": \"%s\"}", base64Content, filename));
                                } else {
                                    // Hvis filen ikke finnes (f.eks. lokal web-sti som ikke mapper til filsystemet direkte)
                                    // så skipper vi den for å unngå feil, eller sender path og håper Resend klarer det (tvilsomt for lokale stier)
                                    logger.warn("Fant ikke vedleggsfil på disk: {}", attachment);
                                    // Prøver å sende som path likevel, i tilfelle Resend har magi (eller det er en public URL uten http prefix)
                                    // Men for å være trygg på Railway, skipper vi hvis vi ikke kan lese den.
                                    if (i > 0) continue;
                                }
                            }
                        } catch (Exception e) {
                            logger.error("Feil ved lesing av vedlegg: {}", attachment, e);
                            continue;
                        }
                    }

                        if (i < attachmentUrls.size() - 1) {
                        attachmentsJson.append(",");
                    }
                }

                // Clean up trailing comma
                if (attachmentsJson.length() > 0 && attachmentsJson.charAt(attachmentsJson.length() - 1) == ',') {
                    attachmentsJson.deleteCharAt(attachmentsJson.length() - 1);
                }

                attachmentsJson.append("]");
            }

            // Sett sammen hele JSON-kroppen
            String jsonBody = String.format(
                    "{\"from\": \"%s\", \"to\": [\"%s\"], \"subject\": \"%s\", \"text\": \"%s\", \"reply_to\": \"%s\"%s}",
                    safeFrom, safeTo, safeSubject, safeText, safeReplyTo, attachmentsJson.toString()
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.resend.com/emails"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + resendApiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200 || response.statusCode() == 201) {
                logger.info("✓ E-post sendt via Resend til: {} (Reply-To: {}) Med vedlegg: {}", to, replyTo, (attachmentUrls != null ? attachmentUrls.size() : 0));
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
        content.append("═══════════════════════\n");
        content.append("NY BESTILLING!\n");
        content.append("═══════════════════════\n");

        content.append("KUNDEINFO:\n");
        content.append("───────────────────────\n");
        content.append("Navn: ").append(customerName).append("\n");
        content.append("Epost: ").append(customerEmail).append("\n");
        content.append("Telefon: ").append(customerPhone).append("\n");
        content.append("Ønsket leveringsdato: ").append(deliveryDate).append("\n\n");

        content.append("BESTILLING:\n");
        content.append("───────────────────────\n");

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
                    content.append("   Bilde: Se vedlegg i denne e-posten.\n");
                }
                content.append("   Estimert pris: ").append(item.getPrice()).append(" kr\n\n");
            }
            itemNumber++;
        }

        content.append("───────────────────────\n");
        content.append("TOTAL: ").append(cartTotal).append(" kr\n\n");

        if (notes != null && !notes.isEmpty()) {
            content.append("NOTATER:\n");
            content.append("───────────────────────\n");
            content.append(notes).append("\n\n");
        }

        content.append("═══════════════════════\n");
        content.append("Ta kontakt med kunden på: ").append(customerPhone).append("\n");
        content.append("eller ").append(customerEmail).append("\n");
        content.append("═══════════════════════\n");

        return content.toString();
    }

    private String buildConfirmationEmailContent(String customerName,
                                                 List<CartService.CartItemDTO> cartItems,
                                                 BigDecimal cartTotal) {
        StringBuilder content = new StringBuilder();
        content.append("Hei ").append(customerName).append("!\n\n");
        content.append("Takk for din bestilling hos ArtCake AS.\n\n");

        content.append("Vi har mottatt din bestilling med følgende detaljer:\n");
        content.append("═══════════════════════\n\n");

        int itemNumber = 1;
        for (CartService.CartItemDTO item : cartItems) {
            content.append(itemNumber).append(". ");
            if ("standard".equals(item.getItemType())) {
                content.append(item.getCakeName()).append(" (").append(item.getSizeCm()).append(" cm)\n");
                content.append("   Antall: ").append(item.getQuantity()).append("\n");
                content.append("   Pris per stk: ").append(item.getPrice()).append(" kr\n");
            } else {
                content.append("Personlig kake\n");
                content.append("   Beskrivelse:\n");
                content.append("   ").append(item.getCustomDescription()).append("\n");
                content.append("   Estimert pris: ").append(item.getPrice()).append(" kr\n");
            }
            itemNumber++;
        }

        content.append("\n═══════════════════════\n");
        content.append("Total: ").append(cartTotal).append(" kr\n\n");

        content.append("Vi vil kontakte deg snart for å bekrefte detaljer og gi deg en endelig pris.\n\n");
        content.append("Takk for at du velger ArtCake!\n\n");
        content.append("Med vennlig hilsen,\nArtCake AS");
        return content.toString();
    }
}