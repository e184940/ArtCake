package taras.artcake.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/debug")
public class DebugController {

    @GetMapping("/check-image/{imageName}")
    public ResponseEntity<String> checkImage(@PathVariable String imageName) {
        try {
            Resource resource = new ClassPathResource("static/images/" + imageName);
            if (resource.exists()) {
                return ResponseEntity.ok("Image exists: " + imageName + " (Size: " + resource.contentLength() + " bytes)");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Image not found: " + imageName);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error checking image: " + e.getMessage());
        }
    }

    @GetMapping("/list-images")
    public ResponseEntity<String> listImages() {
        try {
            Resource imagesDir = new ClassPathResource("static/images/");
            return ResponseEntity.ok("Images directory exists: " + imagesDir.exists());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error listing images: " + e.getMessage());
        }
    }
}
