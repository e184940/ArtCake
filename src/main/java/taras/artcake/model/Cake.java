package taras.artcake.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public class Cake {
    private Long id;

    private String name;

    private String description;

    private String nameEn;

    private String descriptionEn;

    private String imageUrl;

    private String imageUrl2;

    private Set<Allergen> allergens = Set.of();

    private List<CakeSize> sizes;

    public BigDecimal getMinPrice() {
        if (sizes == null || sizes.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return sizes.stream()
                .map(CakeSize::getPrice)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
    }

    public Cake() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescriptionEn() {
        return descriptionEn;
    }

    public void setDescriptionEn(String descriptionEn) {
        this.descriptionEn = descriptionEn;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Set<Allergen> getAllergens() {
        return allergens;
    }

    public void setAllergens(Set<Allergen> allergens) {
        this.allergens = allergens;
    }

    public List<CakeSize> getSizes() {
        return sizes;
    }

    public void setSizes(List<CakeSize> sizes) {
        this.sizes = sizes;
    }

    public String getImageUrl2() {
        return imageUrl2;
    }

    public void setImageUrl2(String imageUrl2) {
        this.imageUrl2 = imageUrl2;
    }
}