package taras.artcake.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import taras.artcake.model.Order;
import taras.artcake.model.OrderItem;
import taras.artcake.repo.OrderRepository;
import taras.artcake.service.CartService;
import taras.artcake.service.EmailService;
import jakarta.servlet.http.HttpSession;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private OrderRepository orderRepository;

    @PostMapping("/add-standard")
    @ResponseBody
    public String addStandardToCart(
            @RequestParam Long cakeId,
            @RequestParam String cakeName,
            @RequestParam Long cakeSizeId,
            @RequestParam Integer sizeCm,
            @RequestParam BigDecimal price,
            HttpSession session) {

        CartService.CartItemDTO item = new CartService.CartItemDTO(
                System.currentTimeMillis(),
                cakeId,
                cakeName,
                cakeSizeId,
                sizeCm,
                price,
                1,
                "standard",
                null,
                null
        );

        cartService.addToCart(session, item);
        return "added";
    }

    @GetMapping("")
    public String viewCart(Model model, HttpSession session) {
        model.addAttribute("cartItems", cartService.getCartItems(session));
        model.addAttribute("cartTotal", cartService.getCartTotal(session));
        return "cart";
    }

    @PostMapping("/remove")
    @ResponseBody
    public String removeFromCart(@RequestParam Long itemId, HttpSession session) {
        cartService.removeFromCart(session, itemId);
        return "removed";
    }

    @PostMapping("/update-quantity")
    @ResponseBody
    public String updateQuantity(@RequestParam Long itemId, @RequestParam int quantity, HttpSession session) {
        cartService.updateQuantity(session, itemId, quantity);
        return "updated";
    }

    @PostMapping("/add-custom")
    @ResponseBody
    public String addCustomToCart(
            @RequestParam String description,
            @RequestParam(required = false) String imageUrl,
            @RequestParam(required = false) MultipartFile inspirationImage,
            @RequestParam BigDecimal price,
            HttpSession session) {

        try {
            String finalImageUrl = imageUrl;

            // Handle file upload if present
            if (inspirationImage != null && !inspirationImage.isEmpty()) {
                try {
                    // Validate file type
                    String contentType = inspirationImage.getContentType();
                    if (contentType == null || !contentType.startsWith("image/")) {
                        throw new IllegalArgumentException("Kun bildefiler er tillatt (JPG, PNG, GIF)");
                    }

                    // Validate file size (max 10MB)
                    if (inspirationImage.getSize() > 10 * 1024 * 1024) {
                        throw new IllegalArgumentException("Bildet er for stort (maks 10MB)");
                    }

                    // Use target directory for uploads (works in both dev and prod for runtime)
                    // In production (jar), we can't write to classpath easily, so we might need an external storage
                    // For this simple app, we'll try to write to a temp folder or the running directory

                    // Prøv å finne en skrivbar mappe.
                    // På Railway (og produksjon generelt med JAR) kan vi IKKE skrive til classpath/static.
                    // Vi må bruke en ekstern mappe eller temp-mappe.

                    Path uploadPath;
                    boolean isLocalDev = Files.exists(Paths.get("src", "main", "resources"));

                    if (isLocalDev) {
                        // Lokal utvikling: Lagre i src så det er persistent og reloades
                        uploadPath = Paths.get("src", "main", "resources", "static", "images", "custom-uploads");
                        if (!Files.exists(uploadPath)) {
                            Files.createDirectories(uploadPath);
                        }
                        // Også kopier til target så det vises med en gang uten restart (håndteres lenger ned)
                    } else {
                        // Produksjon / Railway: Bruk persistent volume hvis tilgjengelig, ellers temp.
                        // Sjekk om /app/uploads finnes (standard mount path for Railway volumes)
                        Path volumePath = Paths.get("/app/uploads");
                        if (Files.exists(volumePath) && Files.isWritable(volumePath)) {
                             uploadPath = volumePath;
                        } else {
                             // Fallback til temp hvis volume ikke er montert
                             uploadPath = Paths.get(System.getProperty("java.io.tmpdir"), "artcake-uploads");
                        }

                        if (!Files.exists(uploadPath)) {
                            Files.createDirectories(uploadPath);
                        }
                    }

                    // Save the uploaded file
                    String originalFilename = inspirationImage.getOriginalFilename();
                    if (originalFilename == null || originalFilename.trim().isEmpty()) {
                        originalFilename = "image.jpg";
                    }

                    // Clean filename to avoid issues
                    String cleanFilename = originalFilename.replaceAll("[^a-zA-Z0-9._-]", "_");
                    String fileName = "custom_" + System.currentTimeMillis() + "_" + cleanFilename;

                    // Save to location
                    Path targetFilePath = uploadPath.resolve(fileName);
                    Files.copy(inspirationImage.getInputStream(), targetFilePath, StandardCopyOption.REPLACE_EXISTING);

                    // Hvis lokal utvikling, prøv å kopiere til target også så det vises i nettleser med en gang
                    if (isLocalDev) {
                        try {
                            Path targetStaticPath = Paths.get("target", "classes", "static", "images", "custom-uploads");
                            if (Files.exists(targetStaticPath.getParent())) { // sjekk at images mappen finnes i target
                                if (!Files.exists(targetStaticPath)) {
                                    Files.createDirectories(targetStaticPath);
                                }
                                Path targetDest = targetStaticPath.resolve(fileName);
                                Files.copy(targetFilePath, targetDest, StandardCopyOption.REPLACE_EXISTING);
                            }
                        } catch (Exception e) {
                            // Ignore copy error in dev
                        }
                    }

                    // Construct the URL/Path for EmailService
                    // VIKTIG: For EmailService må vi vite hvor filen er FYSISK på disken hvis den ikke er en offentlig URL.
                    // Hvis vi lagret i temp, MÅ vi bruke absolutt sti.

                    // For visning i admin-panelet bruker vi nå en egen controller-rute som serverer filen dynamisk
                    finalImageUrl = "/custom-image/" + fileName;

                } catch (Exception e) {
                    // Continue without image but log the error
                    finalImageUrl = null;
                    // Re-throw exception to inform the user
                    throw new RuntimeException("Feil ved opplasting av bilde: " + e.getMessage());
                }
            } else {
                // No image provided, use default or leave empty
            }

            CartService.CartItemDTO item = new CartService.CartItemDTO(
                    System.currentTimeMillis(),
                    null, // No cakeId for custom cakes
                    "Personlig kake",
                    null,
                    0,
                    price,
                    1,
                    "custom",
                    description,
                    finalImageUrl
            );

            cartService.addToCart(session, item);
            return "added";

        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }

    @PostMapping("/checkout")
    public String checkout(
            @RequestParam String customerName,
            @RequestParam String customerEmail,
            @RequestParam String customerPhone,
            @RequestParam String deliveryDate,
            @RequestParam(required = false) String notes,
            Model model,
            HttpSession session) {

        try {
            var cartItems = cartService.getCartItems(session);
            var cartTotal = cartService.getCartTotal(session);

            if (cartItems.isEmpty()) {
                model.addAttribute("error", "Handlekurven er tom");
                return "redirect:/cart";
            }

            // Lagre bestilling i databasen
            Order order = new Order();
            order.setCustomerName(customerName);
            order.setCustomerEmail(customerEmail);
            order.setCustomerPhone(customerPhone);
            order.setDeliveryDate(deliveryDate);
            order.setNotes(notes);
            order.setTotalPrice(cartTotal);
            order.setStatus("Ny");
            order.setOrderDate(LocalDateTime.now());

            // Lagre order først
            Order savedOrder = orderRepository.save(order);

            // Lagre order items
            List<OrderItem> orderItems = new ArrayList<>();
            for (CartService.CartItemDTO cartItem : cartItems) {
                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(savedOrder);
                orderItem.setCakeName(cartItem.getCakeName());
                orderItem.setItemType(cartItem.getItemType());
                orderItem.setQuantity(cartItem.getQuantity());
                orderItem.setPrice(cartItem.getPrice());

                if ("standard".equals(cartItem.getItemType())) {
                    orderItem.setCakeSizeId(cartItem.getCakeSizeId());
                    orderItem.setSizeCm(cartItem.getSizeCm());
                } else if ("custom".equals(cartItem.getItemType())) {
                    orderItem.setCustomDescription(cartItem.getCustomDescription());
                    orderItem.setCustomImageUrl(cartItem.getCustomImageUrl());
                }

                orderItems.add(orderItem);
            }
            savedOrder.setItems(orderItems);
            orderRepository.save(savedOrder);

            // Send email til konditor og kunde
            emailService.sendOrderEmail(customerName, customerEmail, customerPhone,
                    deliveryDate, notes, cartItems, cartTotal);

            // Tøm handlekurven
            cartService.clearCart(session);

            return "order-confirmation";

        } catch (Exception e) {
            model.addAttribute("error", "Feil ved behandling av bestilling: " + e.getMessage());
            return "redirect:/cart";
        }
    }
}

