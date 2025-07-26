package learnk8s.io.knote.controllers

import io.minio.MinioClient
import jakarta.annotation.PostConstruct
import learnk8s.io.knote.config.properties.KnoteProperties
import learnk8s.io.knote.orm.model.Note
import learnk8s.io.knote.orm.repository.NotesRepository
import org.apache.commons.io.IOUtils
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.util.*


@Controller
class KNoteController(
    private val notesRepository: NotesRepository,
    private val properties: KnoteProperties
) {
    private val parser = Parser.builder().build()
    private val renderer = HtmlRenderer.builder().build()
    private var minioClient: MinioClient? = null

    @PostConstruct
    fun init() {
        initMinio()
    }

    @GetMapping("/")
    fun index(model: Model?): String {
        getAllNotes(model!!)
        return "index"
    }

    @PostMapping("/note")
    fun saveNotes(
        @RequestParam("image") file: MultipartFile?,
        @RequestParam description: String?,
        @RequestParam(required = false) publish: String?,
        @RequestParam(required = false) upload: String?,
        model: Model?
    ): String {
        if (publish != null && publish == "Publish") {
            saveNote(description, model!!)
            getAllNotes(model!!)
            return "redirect:/"
        }
        if (upload != null && upload.equals("Upload")) {
            if (file != null && file.originalFilename != null && !file.originalFilename!!.isEmpty()) {
                uploadImage(file, description!!, model!!)
            }
            getAllNotes(model!!)
            return "index"
        }
        // After save fetch all notes again
        return "index"
    }

    @GetMapping(value = ["/img/{name}"], produces = [MediaType.IMAGE_PNG_VALUE])
    @ResponseBody
    @Throws(java.lang.Exception::class)
    fun getImageByName(@PathVariable name: String?): ByteArray {
        val imageStream = minioClient!!.getObject(properties.minioBucket, name)
        return IOUtils.toByteArray(imageStream)
    }

    private fun getAllNotes(model: Model) {
        val notes: List<Note?> = notesRepository.findAll()
        Collections.reverse(notes)
        model.addAttribute("notes", notes)
    }

    private fun saveNote(description: String?, model: Model) {
        if (description != null && !description.trim().isEmpty()) {
            notesRepository.save(Note(null, description.trim()))
            //After publish you need to clean up the textarea
            model.addAttribute("description", "")
        }
    }

    private fun uploadImage(file: MultipartFile, description: String, model: Model) {
//        val uploadsDir: File = File(properties.uploadDir)
//        if (!uploadsDir.exists()) {
//            uploadsDir.mkdir()
//        }
        val fileId = (UUID.randomUUID().toString() + "."
                + file.originalFilename!!.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1])
//        file.transferTo(File(properties.uploadDir + fileId))
        minioClient!!.putObject(properties.minioBucket, fileId, file.getInputStream(),
            file.getSize(), null, null, file.getContentType());
        model.addAttribute("description", "/img/$fileId")
    }

    @Throws(InterruptedException::class)
    private fun initMinio() {
        var success = false
        while (!success) {
            try {
                minioClient = MinioClient(
                    "http://" + properties.minioHost + ":9000",
                    properties.minioAccessKey,
                    properties.minioSecretKey,
                    false
                )
                // Check if the bucket already exists.
                val isExist: Boolean = minioClient!!.bucketExists(properties.minioBucket)
                if (isExist) {
                    println("> Bucket already exists.")
                } else {
                    minioClient!!.makeBucket(properties.minioBucket)
                }
                success = true
            } catch (e: Exception) {
                e.printStackTrace()
                println("> Minio Reconnect: " + properties.minioReconnectEnabled)
                if (properties.minioReconnectEnabled) {
                    try {
                        Thread.sleep(5000)
                    } catch (ex: InterruptedException) {
                        ex.printStackTrace()
                    }
                } else {
                    success = true
                }
            }
        }
        println("> Minio initialized!")
    }
}