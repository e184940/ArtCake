package taras.artcake.service;

import org.springframework.stereotype.Service;
import taras.artcake.model.Allergen;
import taras.artcake.model.Cake;
import taras.artcake.model.CakeSize;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
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
                cake(1L, "Trippel sjokolade", "Triple chocolate", "Mousse kake som består av tre typer belgisk sjokolade. Mørk, hvit, og melkesjokolade", "/images/trippelsjoko_stykke.jpg", "/images/trippelsjoko_hel.jpg", 1, 2, 3, 4),
                cake(2L, "Snickers", "Snickers", "3-lags kake med aromatisk sjokoladebunn av belgisk sjokolade. Fylt med salt karamell, ristede peanøtter, vanilje, og sjokoladekrem", "/images/snickers_stykke.jpg", "/images/snickers_hel.jpg", 1, 2, 3, 4, 5),
                cake(3L, "Kirsebærkake", "Cherry cake", "Myk og saftig bunn av belgisk sjokolade. Fylt med kirsebær, og ostekrem med vanilje", "/images/kirseber_stykke.jpg", "/images/kirseber_hel.jpg", 1, 2, 3, 4),
                cake(4L, "Jordbærmilkshake", "Strawberry milkshake", "Deilig bunn med vaniljesmak. Fylt med ostekrem, vanilje, jordbær, og jordbær marmelade", "/images/jordber_milkshake_stykke.jpg", "/images/jordber_milkshake_hel.jpg", 1, 2, 4),
                cake(5L, "Honningkake", "Honey cake", "Saftig flerlagskake som består av honningkjeks og hjemmelaget vaniljekrem", "/images/honningkake_stykke.jpg", "/images/honningkake_hel.jpg", 1, 2, 4),
                cake(6L, "Bringebærostekake", "Raspberry cheesecake", "Ostekake med bringegbærcoulis, bringebærkrem, og ekte vanilje", "/images/bringeber_ostekake_stykke.jpg", "/images/bringeber_ostekake_hel.jpg", 1, 2, 3, 4),
                singlePieceCake(7L, "Sjokoladepotet", "Chocolate potato", "Myk sjokoladekake med deilig sjokoladekrem, trukket i kokos.", "/images/sjoko_potet.jpeg", 1, 2, 3, 4),
                singlePieceCake(8L, "Eclair", "Eclair", "Luftig vannbakkels fylt med vaniljekrem og toppet med sjokolade.", "/images/eclair.jpeg", 1, 2, 3, 4)
        );
    }

    private Cake cake(Long id, String name, String nameEn, String description, String imageUrl, String imageUrl2, int... allergenIds) {
        Cake cake = new Cake();
        cake.setId(id);
        cake.setName(name);
        cake.setNameEn(nameEn);
        cake.setDescription(description);
        cake.setDescriptionEn("");
        cake.setImageUrl(imageUrl);
        cake.setImageUrl2(imageUrl2);
        cake.setAllergens(allergens(allergenIds));
        cake.setSizes(List.of(
                size((int) (id * 10 + 1), "8-10", 18, 549),
                size((int) (id * 10 + 2), "12-14", 20, 649),
                size((int) (id * 10 + 3), "20", 25, 899),
                size((int) (id * 10 + 4), "25", 28, 1099)
        ));
        return cake;
    }

    private Cake singlePieceCake(Long id, String name, String nameEn, String description, String imageUrl, int... allergenIds) {
        Cake cake = new Cake();
        cake.setId(id);
        cake.setName(name);
        cake.setNameEn(nameEn);
        cake.setDescription(description);
        cake.setDescriptionEn("");
        cake.setImageUrl(imageUrl);
        cake.setAllergens(allergens(allergenIds));
        cake.setSizes(List.of(size((int) (id * 10 + 1), "1", 0, 49)));
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

    private Set<Allergen> allergens(int... allergenIds) {
        LinkedHashSet<Allergen> allergens = new LinkedHashSet<>();
        for (int allergenId : allergenIds) {
            Allergen allergen = new Allergen();
            allergen.setId((long) allergenId);
            allergen.setName(switch (allergenId) {
                case 1 -> "Gluten";
                case 2 -> "Laktose";
                case 3 -> "Soya";
                case 4 -> "Egg";
                case 5 -> "Nøtter";
                default -> throw new IllegalArgumentException("Ukjent allergen-ID: " + allergenId);
            });
            allergens.add(allergen);
        }
        return allergens;
    }
}