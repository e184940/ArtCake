package taras.artcake.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Bilder
        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/");

        // CSS
        registry.addResourceHandler("/css/**")
                .addResourceLocations("classpath:/static/css/");

        // Generelt static (fallback)
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
    }
}

