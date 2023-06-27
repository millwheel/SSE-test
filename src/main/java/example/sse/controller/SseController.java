package example.sse.controller;

import example.sse.service.SseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@RestController
public class SseController {

    private final SseService sseService;

    @Autowired
    public SseController(SseService sseService) {
        this.sseService = sseService;
    }

    @CrossOrigin
    @GetMapping(value="/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public SseEmitter connect(){
        Integer userId = 1;
        SseEmitter sseEmitter = sseService.add(userId);
        sseService.showCount();
        return sseEmitter;
    }

    @CrossOrigin
    @PostMapping("/sse")
    @ResponseStatus(HttpStatus.OK)
    public void order(){
        sseService.addCount();
    }
}
