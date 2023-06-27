package example.sse.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
public class SseService {

    private static final AtomicLong emitterCounter = new AtomicLong();
    ConcurrentHashMap<Integer, SseEmitter> emitterList = new ConcurrentHashMap<>();



    public SseEmitter add(Integer userId) {
        SseEmitter emitter = new SseEmitter();
        emitterList.put(userId, emitter);
        log.info("new emitter added: {}", emitter);
        log.info("emitter list:{}", emitterList);
        emitter.onCompletion(() -> {
            this.emitterList.remove(emitter);
            log.info("emitter deleted: {}", emitter);
        });
        emitter.onTimeout(emitter::complete);
        long count = emitterCounter.get();
        try{
            emitter.send(SseEmitter.event().name("connect").data("connected"));
            emitter.send(SseEmitter.event().name("count").data(count));
        } catch (IOException e){
            throw new RuntimeException(e);
        }
        return emitter;
    }

    public void count() {
        long count = emitterCounter.incrementAndGet();
        emitterList.forEachValue(Long.MAX_VALUE, emitter -> {
            try {
                emitter.send(SseEmitter.event().name("count").data(count));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

}
