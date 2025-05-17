package learnk8s.io.knote.orm.model

import lombok.AllArgsConstructor
import lombok.Getter
import lombok.NoArgsConstructor
import lombok.Setter
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "notes")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
class Note(
    @Id
    private val id: String? = null,
    private val description: String? = null
) {
    override fun toString(): String {
        return description!!
    }
}