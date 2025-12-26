package taras.artcake.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;
import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    private static final String ARTCAKE_EMAIL = "artcake@artcake.no";

    @Value("${app.mail.from:artcake@artcake.no}")
    private String mailFrom;

    @Value("${app.base-url:http://localhost:8080}")
    private String appBaseUrl;

    public void sendOrderEmail(String customerName, String customerEmail, String customerPhone,
                               String deliveryDate, String notes,
                               List<CartService.CartItemDTO> cartItems, BigDecimal cartTotal) {

        logger.info("=== SENDING ORDER EMAIL ===");
        for (CartService.CartItemDTO item : cartItems) {
            if ("custom".equals(item.getItemType())) {
                logger.info("Custom cake item - Description: {}", item.getCustomDescription());
                logger.info("Custom cake item - Image URL: {}", item.getCustomImageUrl());
                if (item.getCustomImageUrl() != null && !item.getCustomImageUrl().trim().isEmpty()) {
                    logger.info("Will send image as EMAIL ATTACHMENT instead of URL");
                }
            }
        }

        // Send email to konditor (ArtCake)
        sendOrderEmailToKonditor(customerName, customerEmail, customerPhone,
                deliveryDate, notes, cartItems, cartTotal);

        // Send confirmation email to customer
        sendConfirmationEmailToCustomer(customerName, customerEmail, cartItems, cartTotal);
    }

    // Send a simple contact message to the konditor and optionally confirm to the sender
    public void sendSimpleContactEmail(String senderName, String senderEmail, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(ARTCAKE_EMAIL);
            message.setFrom(mailFrom);
            message.setSubject("Ny kontaktskjema-melding fra " + senderName);
            StringBuilder sb = new StringBuilder();
            sb.append("Melding fra kontaktskjema:\n\n");
            sb.append("Navn: ").append(senderName).append("\n");
            sb.append("Epost: ").append(senderEmail).append("\n\n");
            sb.append(body).append("\n\n");
            sb.append("--\n");
            sb.append("ArtCake kontaktmelding\n");

            message.setText(sb.toString());
            mailSender.send(message);
            logger.info("Kontaktmelding sendt til konditor: {}", ARTCAKE_EMAIL);
            // Optionally send a simple confirmation to sender
            try {
                SimpleMailMessage conf = new SimpleMailMessage();
                conf.setTo(senderEmail);
                conf.setFrom(mailFrom);
                conf.setSubject("Takk for din melding til ArtCake");
                conf.setText("Hei " + senderName + "\n\nTakk for meldingen! Vi vil kontakte deg snart.\n\nMed vennlig hilsen,\nArtCake AS");
                mailSender.send(conf);
                logger.info("Bekreftelsesmail sendt til avsender: {}", senderEmail);
            } catch (Exception ex) {
                logger.warn("Kunne ikke sende bekreftelsesmail til kunde: {}", ex.getMessage());
            }
        } catch (Exception e) {
            logger.warn("Feil ved sending av kontaktmelding: {}", e.getMessage());
        }
    }

    // Alias method kept for backward compatibility
    public void sendContactMessage(String senderName, String senderEmail, String body) {
        sendSimpleContactEmail(senderName, senderEmail, body);
    }

    private void sendOrderEmailToKonditor(String customerName, String customerEmail, String customerPhone,
                               String deliveryDate, String notes,
                               List<CartService.CartItemDTO> cartItems, BigDecimal cartTotal) {

        try {
            // Check if any custom items have images that need to be attached
            boolean hasCustomImages = cartItems.stream()
                .anyMatch(item -> "custom".equals(item.getItemType()) &&
                         item.getCustomImageUrl() != null &&
                         !item.getCustomImageUrl().trim().isEmpty());

            if (hasCustomImages) {
                // Use MimeMessage for attachments
                MimeMessage mimeMessage = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

                helper.setTo(ARTCAKE_EMAIL);
                helper.setFrom(mailFrom);
                helper.setSubject("Ny bestilling fra " + customerName + " (med inspirasjonsbilde)");

                String emailContent = buildOrderEmailContent(customerName, customerEmail, customerPhone,
                                                           deliveryDate, notes, cartItems, cartTotal);
                helper.setText(emailContent);

                // Attach custom images
                for (CartService.CartItemDTO item : cartItems) {
                    if ("custom".equals(item.getItemType()) &&
                        item.getCustomImageUrl() != null &&
                        !item.getCustomImageUrl().trim().isEmpty()) {

                        String imageUrl = item.getCustomImageUrl();
                        if (imageUrl.startsWith("/imgs/custom-uploads/")) {
                            // Find the actual file
                            String filename = imageUrl.substring("/imgs/custom-uploads/".length());
                            Path imagePath = Paths.get("src/main/resources/static/imgs/custom-uploads/" + filename);

                            if (!Files.exists(imagePath)) {
                                // Try target directory
                                imagePath = Paths.get("target/classes/static/imgs/custom-uploads/" + filename);
                            }

                            if (Files.exists(imagePath)) {
                                FileSystemResource file = new FileSystemResource(imagePath.toFile());
                                helper.addAttachment("Inspirasjonsbilde_" + filename, file);
                                logger.info("Added inspiration image as attachment: {}", filename);
                            } else {
                                logger.warn("Could not find image file for attachment: {}", imagePath);
                            }
                        }
                    }
                }

                mailSender.send(mimeMessage);
                logger.info("Order email with attachments sent to konditor");

            } else {
                // Use simple text message for orders without images
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(ARTCAKE_EMAIL);
                message.setFrom(mailFrom);
                message.setSubject("Ny bestilling fra " + customerName);
                message.setText(buildOrderEmailContent(customerName, customerEmail, customerPhone,
                                                       deliveryDate, notes, cartItems, cartTotal));
                mailSender.send(message);
                logger.info("Simple order email sent to konditor");
            }

        } catch (Exception e) {
            logger.error("Failed to send order email to konditor: {}", e.getMessage(), e);
        }
    }

    private void sendConfirmationEmailToCustomer(String customerName, String customerEmail,
                                                 List<CartService.CartItemDTO> cartItems,
                                                 BigDecimal cartTotal) {

        try {
            // Check if any custom items have images that need to be attached
            boolean hasCustomImages = cartItems.stream()
                .anyMatch(item -> "custom".equals(item.getItemType()) &&
                         item.getCustomImageUrl() != null &&
                         !item.getCustomImageUrl().trim().isEmpty());

            if (hasCustomImages) {
                // Use MimeMessage for attachments
                MimeMessage mimeMessage = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

                helper.setTo(customerEmail);
                helper.setFrom(mailFrom);
                helper.setSubject("Bestillingen din er mottatt - ArtCake AS (med inspirasjonsbilde)");

                String emailContent = buildConfirmationEmailContent(customerName, cartItems, cartTotal);
                helper.setText(emailContent);

                // Attach custom images
                for (CartService.CartItemDTO item : cartItems) {
                    if ("custom".equals(item.getItemType()) &&
                        item.getCustomImageUrl() != null &&
                        !item.getCustomImageUrl().trim().isEmpty()) {

                        String imageUrl = item.getCustomImageUrl();
                        if (imageUrl.startsWith("/imgs/custom-uploads/")) {
                            // Find the actual file
                            String filename = imageUrl.substring("/imgs/custom-uploads/".length());
                            Path imagePath = Paths.get("src/main/resources/static/imgs/custom-uploads/" + filename);

                            if (!Files.exists(imagePath)) {
                                // Try target directory
                                imagePath = Paths.get("target/classes/static/imgs/custom-uploads/" + filename);
                            }

                            if (Files.exists(imagePath)) {
                                FileSystemResource file = new FileSystemResource(imagePath.toFile());
                                helper.addAttachment("Ditt_inspirasjonsbilde_" + filename, file);
                                logger.info("Added inspiration image as attachment for customer: {}", filename);
                            } else {
                                logger.warn("Could not find image file for customer attachment: {}", imagePath);
                            }
                        }
                    }
                }

                mailSender.send(mimeMessage);
                logger.info("Confirmation email with attachments sent to customer");

            } else {
                // Use simple text message for orders without images
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(customerEmail);
                message.setFrom(mailFrom);
                message.setSubject("Bestillingen din er mottatt - ArtCake AS");
                message.setText(buildConfirmationEmailContent(customerName, cartItems, cartTotal));
                mailSender.send(message);
                logger.info("Simple confirmation email sent to customer");
            }

        } catch (Exception e) {
            logger.error("Failed to send confirmation email to customer: {}", e.getMessage(), e);
        }
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
                content.append(item.getCakeName()).append(" (").append(item.getSizeCm())
                        .append(" cm)\n");
                content.append("   Pris: ").append(item.getPrice()).append(" kr\n\n");
            } else if ("custom".equals(item.getItemType())) {
                content.append("PERSONLIG KAKE\n");
                content.append("   Beskrivelse: ").append(item.getCustomDescription()).append("\n");

                // Add inspiration image if present
                String imageUrl = item.getCustomImageUrl();
                if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                    logger.info("Custom cake has inspiration image: {}", imageUrl);
                    content.append("   Inspirasjonsbilde: Se vedlagte fil\n");
                } else {
                    logger.info("No inspiration image provided for custom cake");
                    content.append("   Inspirasjonsbilde: Ingen bilde vedlagt\n");
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

        content.append("Takk for din bestilling hos ArtCake AS!\n\n");

        content.append("Vi har mottatt din bestilling med følgende detaljer:\n");
        content.append("═══════════════════════════════════════\n\n");

        int itemNumber = 1;
        for (CartService.CartItemDTO item : cartItems) {
            content.append(itemNumber).append(". ");

            if ("standard".equals(item.getItemType())) {
                content.append(item.getCakeName()).append(" (").append(item.getSizeCm()).append(" cm)\n");
                content.append("   Pris: ").append(item.getPrice()).append(" kr\n\n");
            } else if ("custom".equals(item.getItemType())) {
                content.append("Personlig kake\n");
                content.append("   ").append(item.getCustomDescription()).append("\n");

                // Add inspiration image if present
                String imageUrl = item.getCustomImageUrl();
                if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                    logger.info("Custom cake confirmation has inspiration image: {}", imageUrl);
                    content.append("   Inspirasjonsbilde: Se vedlagte fil\n");
                } else {
                    content.append("   Inspirasjonsbilde: Ingen bilde vedlagt\n");
                }

                content.append("   Estimert pris: ").append(item.getPrice()).append(" kr\n\n");
            }
            itemNumber++;
        }

        content.append("═══════════════════════════════════════\n");
        content.append("Total: ").append(cartTotal).append(" kr\n\n");

        content.append("En av våre kakemestere vil kontakte deg snart for å bekrefte detaljer og gi deg en endelig pris.\n\n");

        content.append("Takk for at du velger ArtCake!\n\n");

        content.append("Med vennlig hilsen,\n");
        content.append("ArtCake AS\n");
        content.append("Telefon: kontakt oss via bestillingen\n");
        content.append("Epost: artcake@artcake.no\n");

        return content.toString();
    }

    // Simple raw send method useful for test endpoints. Returns true on success.
    public boolean sendRawEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setFrom(mailFrom);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            logger.info("✓ Raw email sendt til {}", to);
            return true;
        } catch (Exception e) {
            logger.warn("✗ Feil ved sending av raw email til {}: {}", to, e.getMessage());
            return false;
        }
    }
}

