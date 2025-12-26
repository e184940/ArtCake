package taras.artcake.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.util.StreamUtils;

import java.io.IOException;

@RestController
@RequestMapping("/images")
public class ImageController {

    @GetMapping("/{imageName:.+}")
    public ResponseEntity<byte[]> getImage(@PathVariable String imageName) {
        try {
            Resource resource = new ClassPathResource("static/images/" + imageName);
            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            byte[] imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());

            // Determine media type based on file extension
            MediaType mediaType = MediaType.IMAGE_JPEG; // default
            if (imageName.toLowerCase().endsWith(".png")) {
                mediaType = MediaType.IMAGE_PNG;
            }

            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .body(imageBytes);

        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
