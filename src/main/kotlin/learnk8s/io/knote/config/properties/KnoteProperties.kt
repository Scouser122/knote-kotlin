package learnk8s.io.knote.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties


@ConfigurationProperties(prefix = "knote")
class KnoteProperties {
    var uploadDir: String? = null
}