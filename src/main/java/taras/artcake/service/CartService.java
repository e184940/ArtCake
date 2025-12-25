package taras.artcake.service;

import org.springframework.stereotype.Service;
import taras.artcake.model.OrderItem;
import jakarta.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartService implements Serializable {

    public static class CartItemDTO {
        public Long id;
        public String cakeName;
        public Long cakeSizeId;
        public Integer sizeCm;
        public java.math.BigDecimal price;
        public Integer quantity;
        public String itemType; // "standard" eller "custom"
        public String customDescription;
        public String customImageUrl;

        public CartItemDTO(Long id, String cakeName, Long cakeSizeId, Integer sizeCm,
                          java.math.BigDecimal price, Integer quantity, String itemType,
                          String customDescription, String customImageUrl) {
            this.id = id;
            this.cakeName = cakeName;
            this.cakeSizeId = cakeSizeId;
            this.sizeCm = sizeCm;
            this.price = price;
            this.quantity = quantity;
            this.itemType = itemType;
            this.customDescription = customDescription;
            this.customImageUrl = customImageUrl;
        }

        // Getters for JSP access
        public Long getId() { return id; }
        public String getCakeName() { return cakeName; }
        public Long getCakeSizeId() { return cakeSizeId; }
        public Integer getSizeCm() { return sizeCm; }
        public java.math.BigDecimal getPrice() { return price; }
        public Integer getQuantity() { return quantity; }
        public String getItemType() { return itemType; }
        public String getCustomDescription() { return customDescription; }
        public String getCustomImageUrl() { return customImageUrl; }
    }

    public List<CartItemDTO> getCartItems(jakarta.servlet.http.HttpSession session) {
        List<CartItemDTO> cart = (List<CartItemDTO>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute("cart", cart);
        }
        return cart;
    }

    public void addToCart(HttpSession session, CartItemDTO item) {
        List<CartItemDTO> cart = getCartItems(session);
        cart.add(item);
        session.setAttribute("cart", cart);
    }

    public void removeFromCart(HttpSession session, Long itemId) {
        List<CartItemDTO> cart = getCartItems(session);
        cart.removeIf(item -> item.id.equals(itemId));
        session.setAttribute("cart", cart);
    }

    public void clearCart(HttpSession session) {
        session.setAttribute("cart", new ArrayList<CartItemDTO>());
    }

    public java.math.BigDecimal getCartTotal(HttpSession session) {
        List<CartItemDTO> cart = getCartItems(session);
        return cart.stream()
            .map(item -> item.price.multiply(java.math.BigDecimal.valueOf(item.quantity)))
            .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
    }
}

