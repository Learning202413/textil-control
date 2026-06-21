package pe.edu.upla.textil_control.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import pe.edu.upla.textil_control.interceptor.AuthInterceptor;

import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**") // Intercepta todo
                .excludePathPatterns(
                        "/login",
                        "/setup",
                        "/css/**",
                        "/js/**",
                        "/img/**",
                        "/favicon.ico"
                ); // Ignora recursos públicos
    }

    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Sirve los archivos de la misma forma que tu ImagenServlet original
        String uploadsPath = Paths.get("uploads").toAbsolutePath().toUri().toString();
        registry.addResourceHandler("/uploads/**").addResourceLocations(uploadsPath);
    }
}