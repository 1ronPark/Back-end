package umc.lightup.notification.repository;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class EmitterRepository {
  private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

  public void deleteById(String id) {emitters.remove(id);}
  public void save(String id, SseEmitter emitter) {emitters.put(id, emitter);}

  public Map<String, SseEmitter> findAllById(String id) {
    return emitters.entrySet().stream()
            .filter(entry -> entry.getKey().startsWith(id + "_"))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  public boolean existsByIdStartWith(String id) {
    return emitters.entrySet().stream()
            .anyMatch(entry -> entry.getKey().startsWith(id + "_"));
  }
}
