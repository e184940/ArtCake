package taras.artcake.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class DebugController {

    @GetMapping("/debug/files")
    public List<String> listFiles() throws IOException {
        List<String> fileNames = new ArrayList<>();
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        // Look for all files in static/images
        Resource[] resources = resolver.getResources("classpath*:static/images/*");

        for (Resource resource : resources) {
            String filename = resource.getFilename();
            fileNames.add(filename + " (len: " + filename.length() + ")");
        }

        return fileNames;
    }
}
