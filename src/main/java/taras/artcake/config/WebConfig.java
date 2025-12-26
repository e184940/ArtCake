package taras.artcake.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Images first - most specific
        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/");

        // CSS
        registry.addResourceHandler("/css/**")
                .addResourceLocations("classpath:/static/css/");
    }
}
