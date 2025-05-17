package learnk8s.io.knote.config

import learnk8s.io.knote.config.properties.KnoteProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.resource.PathResourceResolver

@Configuration
@EnableConfigurationProperties(KnoteProperties::class)
class KnoteConfig(
    private val properties: KnoteProperties
) : WebMvcConfigurer {
    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry
            .addResourceHandler("/uploads/**")
            .addResourceLocations("file:" + properties.uploadDir)
            .setCachePeriod(3600)
            .resourceChain(true)
            .addResolver(PathResourceResolver())
    }
}