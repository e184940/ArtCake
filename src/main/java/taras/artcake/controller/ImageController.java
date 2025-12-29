package taras.artcake.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ImageController {

    private static final Logger logger = LoggerFactory.getLogger(ImageController.class);

    @GetMapping({"/custom-image/{filename:.+}", "/images/custom-uploads/{filename:.+}"})
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        try {
            List<Path> possibleLocations = new ArrayList<>();

            // Sjekk om vi er lokalt (src/main/resources finnes)
            boolean isLocalDev = Files.exists(Paths.get("src", "main", "resources"));

            if (isLocalDev) {
                // Lokal utvikling
                possibleLocations.add(Paths.get("src", "main", "resources", "static", "images", "custom-uploads").resolve(filename));
                possibleLocations.add(Paths.get("target", "classes", "static", "images", "custom-uploads").resolve(filename));
            } else {
                // Produksjon (Railway volume)
                possibleLocations.add(Paths.get("/app/uploads").resolve(filename));
                // Fallback temp
                possibleLocations.add(Paths.get(System.getProperty("java.io.tmpdir"), "artcake-uploads").resolve(filename));
            }

            for (Path file : possibleLocations) {
                if (Files.exists(file) && Files.isReadable(file)) {
                    Resource resource = new UrlResource(file.toUri());
                    return ResponseEntity.ok()
                            .contentType(MediaType.IMAGE_JPEG)
                            .body(resource);
                }
            }

            logger.warn("Image not found: {}", filename);
            return ResponseEntity.notFound().build();

        } catch (MalformedURLException e) {
            logger.error("Malformed URL: {}", filename, e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error serving image: {}", filename, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
