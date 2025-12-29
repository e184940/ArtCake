package taras.artcake.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import taras.artcake.repo.CakeRepository;

@Controller
public class ProductsController {

    @Autowired
    private CakeRepository cakeRepo;

    @GetMapping("/products")
    public String productList(Model model) {
        model.addAttribute("cakes", cakeRepo.findAll());
        return "product-list";
    }

    @GetMapping("/products/{id}")
    public String productDetails(@PathVariable Integer id, Model model) {
        model.addAttribute("cake", cakeRepo.findById(id).orElse(null));
        return "product-details";
    }

    @GetMapping("/products/{id}/details")
    public String getProductDetailsModal(@PathVariable Integer id, Model model) {
        model.addAttribute("cake", cakeRepo.findById(id).orElse(null));
        return "product-details"; // This JSP should be a fragment, not a full page
    }

    @GetMapping("/")
    public String home() {
        return "homepage";
    }
}