package taras.artcake.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import taras.artcake.model.Order;
import taras.artcake.repo.OrderRepository;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private OrderRepository orderRepository;

    @GetMapping("")
    public String adminDashboard(@RequestParam(required = false) String filter, Model model) {
        List<Order> orders;

        if ("delivery".equals(filter)) {
            // Sorter på leveringsfrist (nærmeste først)
            orders = orderRepository.findAllByOrderByDeliveryDateAsc();
        } else if ("new".equals(filter)) {
            // Vis kun nye
            orders = orderRepository.findByStatus("Ny");
        } else {
            // Standard: Nyeste bestillinger først (innsendt dato)
            orders = orderRepository.findAllByOrderByOrderDateDesc();
        }

        model.addAttribute("orders", orders);
        return "admin";
    }

    @PostMapping("/update-status")
    public String updateStatus(@RequestParam Long orderId, @RequestParam String status) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order != null) {
            order.setStatus(status);
            orderRepository.save(order);
        }
        return "redirect:/admin";
    }
}

