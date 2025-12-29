package taras.artcake.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.time.Duration;
import java.util.Locale;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Images first - most specific
        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/", "file:src/main/resources/static/images/");

        // CSS
        registry.addResourceHandler("/css/**")
                .addResourceLocations("classpath:/static/css/");

        // Generelt static (fallback)
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
    }

    @Bean
    public LocaleResolver localeResolver() {
        CookieLocaleResolver localeResolver = new CookieLocaleResolver("lang");
        localeResolver.setDefaultLocale(new Locale("no"));
        localeResolver.setCookieMaxAge(Duration.ofDays(365)); // Remember for 1 year
        localeResolver.setCookiePath("/"); // ensure cookie is available site-wide
        return localeResolver;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
        localeChangeInterceptor.setParamName("lang");
        return localeChangeInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // First, allow ?lang= to change/resolver the locale and have it stored in cookie
        registry.addInterceptor(localeChangeInterceptor());

        // Then add an interceptor that exposes the resolved language to JSPs as request attribute `currentLang`.
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                Locale resolved = localeResolver().resolveLocale(request);
                request.setAttribute("currentLang", resolved.getLanguage());
                return true;
            }
        });
    }

    @Bean
    public org.springframework.context.MessageSource messageSource() {
        org.springframework.context.support.ReloadableResourceBundleMessageSource messageSource = new org.springframework.context.support.ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
}
