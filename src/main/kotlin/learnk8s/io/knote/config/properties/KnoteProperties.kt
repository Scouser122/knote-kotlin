package learnk8s.io.knote.config.properties

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties


@ConfigurationProperties(prefix = "knote")
class KnoteProperties {
    var uploadDir: String? = null

    @Value("\${minio.host:localhost}")
    var minioHost: String? = null

    @Value("\${minio.bucket:image-storage}")
    var minioBucket: String? = null

    @Value("\${minio.access.key:mykey}")
    var minioAccessKey: String? = null

    @Value("\${minio.secret.key:mysecret}")
    var minioSecretKey: String? = null

    @Value("\${minio.useSSL:false}")
    var minioUseSSL = false

    @Value("\${minio.reconnect.enabled:true}")
    var minioReconnectEnabled = false
}