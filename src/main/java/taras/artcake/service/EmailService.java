package taras.artcake.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    private static final String ARTCAKE_EMAIL = "artcake@artcake.no";

    public void sendOrderEmail(String customerName, String customerEmail, String customerPhone,
                               String deliveryDate, String notes,
                               List<CartService.CartItemDTO> cartItems, BigDecimal cartTotal) {

        // Send email to konditor (ArtCake)
        sendOrderEmailToKonditor(customerName, customerEmail, customerPhone,
                deliveryDate, notes, cartItems, cartTotal);

        // Send confirmation email to customer
        sendConfirmationEmailToCustomer(customerName, customerEmail, cartItems, cartTotal);
    }

    private void sendOrderEmailToKonditor(String customerName, String customerEmail, String customerPhone,
                               String deliveryDate, String notes,
                               List<CartService.CartItemDTO> cartItems, BigDecimal cartTotal) {

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(ARTCAKE_EMAIL);
            message.setFrom("artcake@artcake.no");
            message.setSubject("📦 Ny bestilling fra " + customerName);
            message.setText(buildOrderEmailContent(customerName, customerEmail, customerPhone,
                                                   deliveryDate, notes, cartItems, cartTotal));

            logger.info("Sender bestillingsepost til konditor: " + ARTCAKE_EMAIL);
            mailSender.send(message);
            logger.info("✓ Bestillingsepost sendt til konditor");
        } catch (Exception e) {
            logger.warn("⚠ Email-sending feilet (bestillingen er likevel lagret): " + e.getMessage());
        }
    }

    private void sendConfirmationEmailToCustomer(String customerName, String customerEmail,
                                                 List<CartService.CartItemDTO> cartItems,
                                                 BigDecimal cartTotal) {

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(customerEmail);
            message.setFrom("artcake@artcake.no");
            message.setSubject("✓ Bestillingen din er mottatt - ArtCake AS");
            message.setText(buildConfirmationEmailContent(customerName, cartItems, cartTotal));

            logger.info("Sender bekreftelsesmail til kunde: " + customerEmail);
            mailSender.send(message);
            logger.info("✓ Bekreftelsesmail sendt til kunde");
        } catch (Exception e) {
            logger.warn("⚠ Email-sending feilet (bestillingen er likevel lagret): " + e.getMessage());
        }
    }

    private String buildOrderEmailContent(String customerName, String customerEmail,
                                          String customerPhone, String deliveryDate,
                                          String notes, List<CartService.CartItemDTO> cartItems,
                                          BigDecimal cartTotal) {

        StringBuilder content = new StringBuilder();
        content.append("═══════════════════════════════════════\n");
        content.append("NY BESTILLING - ARTCAKE\n");
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
}

