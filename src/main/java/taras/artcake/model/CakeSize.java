package taras.artcake.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "cake_sizes")
public class CakeSize {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "cake_id")
    private Cake cake;

    private int sizeCm;
    private BigDecimal price;

    public CakeSize(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Cake getCake() {
        return cake;
    }

    public void setCake(Cake cake) {
        this.cake = cake;
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

