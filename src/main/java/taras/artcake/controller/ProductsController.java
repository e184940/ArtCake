package taras.artcake.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import taras.artcake.repo.CakeRepo;

@Controller
public class ProductsController {

    @Autowired
    private CakeRepo cakeRepository;

    @GetMapping("/products")
    public String productList(Model model) {
        model.addAttribute("cakes", cakeRepository.findAll());
        return "product-list";
    }

    @GetMapping("/products/{id}")
    public String productDetails(@PathVariable Integer id, Model model) {
        model.addAttribute("cake", cakeRepository.findById(id).orElse(null));
        return "product-details";
    }


}