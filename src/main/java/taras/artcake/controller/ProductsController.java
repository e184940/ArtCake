package taras.artcake.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import taras.artcake.service.CakeCatalogService;

@Controller
public class ProductsController {

    private final CakeCatalogService cakeCatalogService;

    public ProductsController(CakeCatalogService cakeCatalogService) {
        this.cakeCatalogService = cakeCatalogService;
    }

    @GetMapping("/products")
    public String productList(Model model) {
        model.addAttribute("cakes", cakeCatalogService.findAll());
        return "product-list";
    }

    @GetMapping("/products/{id}")
    public String productDetails(@PathVariable Long id, Model model) {
        model.addAttribute("cake", cakeCatalogService.findById(id));
        return "product-details";
    }

    @GetMapping("/products/{id}/details")
    public String getProductDetailsModal(@PathVariable Long id, Model model) {
        model.addAttribute("cake", cakeCatalogService.findById(id));
        return "product-details"; // This JSP should be a fragment, not a full page
    }

    @GetMapping("/")
    public String home() {
        return "homepage";
    }
}