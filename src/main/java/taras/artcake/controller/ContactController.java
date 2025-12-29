package taras.artcake.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import taras.artcake.model.Message;
import taras.artcake.repo.MessageRepository;
import taras.artcake.service.EmailService;

@Controller
public class ContactController {

    @Autowired
    private MessageRepository messageRepo;

    @Autowired
    private EmailService emailService;

    @PostMapping("/contact/send")
    public String sendContactMessage(@RequestParam String name,
                                     @RequestParam String email,
                                     @RequestParam String message,
                                     RedirectAttributes ra) {
        try {
            // Save message
            Message m = new Message(name, email, message);
            messageRepo.save(m);

            // Send notification email to konditor
            // We'll use EmailService to send a simple email to artcake
            emailService.sendContactMessage(name, email, message);

            ra.addFlashAttribute("contactSent", true);
            return "redirect:/contact?sent=true";
        } catch (Exception e) {
            ra.addFlashAttribute("contactError", "Det oppstod en feil. Prøv igjen senere.");
            return "redirect:/contact?sent=false";
        }
    }

    @GetMapping("/contact")
    public String contact() {
        return "contact";
    }

    @GetMapping("/faq")
    public String faq() {
        return "faq";
    }

    @GetMapping("/reviews")
    public String reviews() {
        return "reviews";
    }

    @GetMapping("/terms")
    public String terms() {
        return "terms";
    }
}
