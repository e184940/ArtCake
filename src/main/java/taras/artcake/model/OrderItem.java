package taras.artcake.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "cake_id")
    private Cake cake;

    @Column(name = "cake_size_id")
    private Long cakeSizeId;

    @Column(name = "size_cm")
    private Integer sizeCm;

    @Column(name = "cake_name")
    private String cakeName;

    private Integer quantity;
    private BigDecimal price;

    @Column(columnDefinition = "TEXT")
    private String customDescription;

    @Column(name = "custom_image_url")
    private String customImageUrl;

    private String itemType; // "standard" eller "custom"

    public OrderItem() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Cake getCake() {
        return cake;
    }

    public void setCake(Cake cake) {
        this.cake = cake;
    }

    public Long getCakeSizeId() {
        return cakeSizeId;
    }

    public void setCakeSizeId(Long cakeSizeId) {
        this.cakeSizeId = cakeSizeId;
    }

    public Integer getSizeCm() {
        return sizeCm;
    }

    public void setSizeCm(Integer sizeCm) {
        this.sizeCm = sizeCm;
    }

    public String getCakeName() {
        return cakeName;
    }

    public void setCakeName(String cakeName) {
        this.cakeName = cakeName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getCustomDescription() {
        return customDescription;
    }

    public void setCustomDescription(String customDescription) {
        this.customDescription = customDescription;
    }

    public String getCustomImageUrl() {
        return customImageUrl;
    }

    public void setCustomImageUrl(String customImageUrl) {
        this.customImageUrl = customImageUrl;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }
}

