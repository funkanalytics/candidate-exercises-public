package com.imply.architect.candidateapplication.controller

import org.apache.commons.logging.LogFactory
import org.springframework.http.MediaType
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ThreadLocalRandom

@RestController
class FileStreamRestController(private val  fileStreamService: FileStreamService) {
    @GetMapping(value= ["/start-stream"], produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun filePaths() = fileStreamService.filePathStream()
}

@Controller
class fileSteamRSocketController(private val fileStreamService: FileStreamService) {
    @MessageMapping("filePathStream")
    fun filePaths() = fileStreamService.filePathStream()
}

@Service
class FileStreamService {
    private val filePaths = ConcurrentHashMap<String, Flux<String>>()
    private val log = LogFactory.getLog(javaClass)

    fun filePathStream(): Flux<String> {
        return filePaths.computeIfAbsent("testing") {
            Flux
                    .interval(Duration.ofSeconds(1))  // add watcher timer config value
                    .map{ randomString() }
                    .doOnSubscribe { log.info("New subscription for file directory") }
                    .share()
        }
    }

    private fun randomString() = "test_string_"+ ThreadLocalRandom.current().nextLong(100L).toString()
}
