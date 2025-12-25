package taras.artcake.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import taras.artcake.model.Order;
import taras.artcake.model.OrderItem;
import taras.artcake.repository.OrderRepository;
import taras.artcake.service.CartService;
import taras.artcake.service.EmailService;
import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {

    private static final Logger logger = LoggerFactory.getLogger(CartController.class);

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

    @PostMapping("/add-custom")
    @ResponseBody
    public String addCustomToCart(
            @RequestParam String description,
            @RequestParam(required = false) String imageUrl,
            @RequestParam BigDecimal price,
            HttpSession session) {

        CartService.CartItemDTO item = new CartService.CartItemDTO(
            System.currentTimeMillis(),
            "Personlig kake",
            null,
            0,
            price,
            1,
            "custom",
            description,
            imageUrl
        );

        cartService.addToCart(session, item);
        return "added";
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
            logger.info("=== CHECKOUT START ===");
            logger.info("Kunde: " + customerName + " (" + customerEmail + ")");

            var cartItems = cartService.getCartItems(session);
            var cartTotal = cartService.getCartTotal(session);

            logger.info("Handlekurv items: " + cartItems.size());

            if (cartItems.isEmpty()) {
                logger.warn("Handlekurven er tom!");
                model.addAttribute("error", "Handlekurven er tom");
                return "redirect:/cart";
            }

            // Lagre bestilling i databasen
            logger.info("Lagrer bestilling i database...");
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
            logger.info("✓ Bestilling lagret med ID: " + savedOrder.getId());

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
            logger.info("✓ Order items lagret: " + orderItems.size());

            // Send email til konditor og kunde
            logger.info("Sender eposter...");
            emailService.sendOrderEmail(customerName, customerEmail, customerPhone,
                                       deliveryDate, notes, cartItems, cartTotal);

            // Tøm handlekurven
            cartService.clearCart(session);
            logger.info("✓ Handlekurven tømt");

            logger.info("=== CHECKOUT FULLFØRT ===");
            return "order-confirmation";

        } catch (Exception e) {
            logger.error("✗ FEIL I CHECKOUT: " + e.getMessage(), e);
            model.addAttribute("error", "Feil ved behandling av bestilling: " + e.getMessage());
            return "redirect:/cart";
        }
    }
}

