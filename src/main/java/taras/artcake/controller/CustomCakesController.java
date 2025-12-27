package taras.artcake.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CustomCakesController {

    // @GetMapping("/custom-cakes") moved to PageController

    @GetMapping("/order/custom")
    public String customCakeForm() {
        return "custom-cake-form";
    }

    // @GetMapping("/about") removed/unused
}

