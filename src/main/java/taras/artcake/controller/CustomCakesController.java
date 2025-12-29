package taras.artcake.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CustomCakesController {

    @GetMapping("/order/custom")
    public String customCakeForm() {
        return "custom-cake-form";
    }

    @GetMapping("/custom-cakes")
    public String customCakes() {
        return "custom-cakes";
    }
}
