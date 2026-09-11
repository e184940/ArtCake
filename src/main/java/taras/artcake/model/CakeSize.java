package taras.artcake.model;

import java.math.BigDecimal;

public class CakeSize {
    private int id;

    private String servings;
    private int sizeCm;
    private BigDecimal price;

    public CakeSize() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getServings() {
        return servings;
    }

    public void setServings(String servings) {
        this.servings = servings;
    }

    public int getSizeCm() {
        return sizeCm;
    }

    public void setSizeCm(int sizeCm) {
        this.sizeCm = sizeCm;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}

