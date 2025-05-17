package learnk8s.io.knote.orm.repository

import learnk8s.io.knote.orm.model.Note
import org.springframework.data.mongodb.repository.MongoRepository

interface NotesRepository : MongoRepository<Note?, String?>