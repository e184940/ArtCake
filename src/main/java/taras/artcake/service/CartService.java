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
        public Long id; // Unique ID for the cart item entry
        public Long cakeId; // ID of the cake product (for merging)
        public String cakeName;
        public Long cakeSizeId;
        public Integer sizeCm;
        public java.math.BigDecimal price;
        public Integer quantity;
        public String itemType; // "standard" eller "custom"
        public String customDescription;
        public String customImageUrl;

        public CartItemDTO(Long id, Long cakeId, String cakeName, Long cakeSizeId, Integer sizeCm,
                           java.math.BigDecimal price, Integer quantity, String itemType,
                           String customDescription, String customImageUrl) {
            this.id = id;
            this.cakeId = cakeId;
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
        public Long getCakeId() { return cakeId; }
        public String getCakeName() { return cakeName; }
        public Long getCakeSizeId() { return cakeSizeId; }
        public Integer getSizeCm() { return sizeCm; }
        public java.math.BigDecimal getPrice() { return price; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
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

    public void addToCart(HttpSession session, CartItemDTO newItem) {
        List<CartItemDTO> cart = getCartItems(session);
        boolean found = false;

        // Only merge standard items
        if ("standard".equals(newItem.itemType)) {
            for (CartItemDTO item : cart) {
                if ("standard".equals(item.itemType) &&
                        item.cakeId != null && item.cakeId.equals(newItem.cakeId) &&
                        item.cakeSizeId != null && item.cakeSizeId.equals(newItem.cakeSizeId)) {
                    item.quantity += newItem.quantity;
                    found = true;
                    break;
                }
            }
        }

        if (!found) {
            cart.add(newItem);
        }
        session.setAttribute("cart", cart);
    }

    public void updateQuantity(HttpSession session, Long itemId, int newQuantity) {
        List<CartItemDTO> cart = getCartItems(session);
        if (newQuantity <= 0) {
            removeFromCart(session, itemId);
        } else {
            for (CartItemDTO item : cart) {
                if (item.id.equals(itemId)) {
                    item.quantity = newQuantity;
                    break;
                }
            }
            session.setAttribute("cart", cart);
        }
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

