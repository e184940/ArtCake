package taras.artcake.service;

import org.springframework.stereotype.Service;
import taras.artcake.model.Cake;
import taras.artcake.model.CakeSize;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CakeCatalogService {

    private final Map<Long, Cake> cakesById = createCatalog().stream()
            .collect(Collectors.toUnmodifiableMap(Cake::getId, cake -> cake));

    public List<Cake> findAll() {
        return List.copyOf(cakesById.values());
    }

    public Cake findById(Long id) {
        return cakesById.get(id);
    }

    private List<Cake> createCatalog() {
        return List.of(
                cake(1L, "Trippel sjokolade", "Triple chocolate", "/images/trippelsjoko_stykke.jpg", "/images/trippelsjoko_hel.jpg"),
                cake(2L, "Snickers", "Snickers", "/images/snickers_stykke.jpg", "/images/snickers_hel.jpg"),
                cake(3L, "Kirsebaerkake", "Cherry cake", "/images/kirseber_stykke.jpg", "/images/kirseber_hel.jpg"),
                cake(4L, "Jordbaermilkshake", "Strawberry milkshake", "/images/jordber_milkshake_stykke.jpg", "/images/jordber_milkshake_hel.jpg"),
                cake(5L, "Honningkake", "Honey cake", "/images/honningkake_stykke.jpg", "/images/honningkake_hel.jpg"),
                cake(6L, "Bringebaerostekake", "Raspberry cheesecake", "/images/bringeber_ostekake_stykke.jpg", "/images/bringeber_ostekake_hel.jpg")
        );
    }

    private Cake cake(Long id, String name, String nameEn, String imageUrl, String imageUrl2) {
        Cake cake = new Cake();
        cake.setId(id);
        cake.setName(name);
        cake.setNameEn(nameEn);
        cake.setDescription("");
        cake.setDescriptionEn("");
        cake.setImageUrl(imageUrl);
        cake.setImageUrl2(imageUrl2);
        cake.setAllergens(Set.of());
        cake.setSizes(List.of(
                size((int) (id * 10 + 1), "8-10", 18, 549),
                size((int) (id * 10 + 2), "12-14", 20, 649),
                size((int) (id * 10 + 3), "20", 25, 899),
                size((int) (id * 10 + 4), "25", 28, 1099)
        ));
        return cake;
    }

    private CakeSize size(int id, String servings, int sizeCm, int price) {
        CakeSize size = new CakeSize();
        size.setId(id);
        size.setServings(servings);
        size.setSizeCm(sizeCm);
        size.setPrice(BigDecimal.valueOf(price));
        return size;
    }
}