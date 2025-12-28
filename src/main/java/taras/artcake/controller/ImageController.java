package taras.artcake.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Files;

@Controller
public class ImageController {

    private static final Logger logger = LoggerFactory.getLogger(ImageController.class);

    @GetMapping({"/custom-image/{filename:.+}", "/images/custom-uploads/{filename:.+}"})
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        try {
            // Vi sjekker flere steder for å være sikker på å finne filen
            List<Path> possibleLocations = new ArrayList<>();

            // Sjekk om vi er lokalt eller prod (samme logikk som CartController)
            boolean isLocalDev = java.nio.file.Files.exists(Paths.get("src", "main", "resources"));

            if (isLocalDev) {
                // 1. Sjekk src (lokal utvikling - persistent)
                possibleLocations.add(Paths.get("src", "main", "resources", "static", "images", "custom-uploads").resolve(filename));
                // 2. Sjekk target (lokal kjøring - build output)
                possibleLocations.add(Paths.get("target", "classes", "static", "images", "custom-uploads").resolve(filename));
            } else {
                // 3. Sjekk persistent volume (Railway)
                possibleLocations.add(Paths.get("/app/uploads").resolve(filename));

                // 4. Sjekk temp (fallback)
                possibleLocations.add(Paths.get(System.getProperty("java.io.tmpdir"), "artcake-uploads").resolve(filename));
            }

            for (Path file : possibleLocations) {
                if (Files.exists(file) && Files.isReadable(file)) {
                    logger.info("Serving image: {} from path: {}", filename, file.toAbsolutePath());
                    Resource resource = new UrlResource(file.toUri());
                    return ResponseEntity.ok()
                            .contentType(MediaType.IMAGE_JPEG)
                            .body(resource);
                }
            }

            logger.warn("Image not found in any location: {}", filename);
            return ResponseEntity.notFound().build();

        } catch (MalformedURLException e) {
            logger.error("Malformed URL for image: {}", filename, e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error serving image: {}", filename, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}

