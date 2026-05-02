package kr.hhplus.be.server.tokenqueue.presentation.controller;

import kr.hhplus.be.server.tokenqueue.application.port.in.TokenQueueUseCase;
import kr.hhplus.be.server.tokenqueue.presentation.dto.TokenQueueRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/queue/token")
public class TokenQueueController {
    private final TokenQueueUseCase tokenQueueUseCase;

    @PostMapping("/enqueue")
    public ResponseEntity<Void> enqueue(@RequestBody TokenQueueRequest request) {
        tokenQueueUseCase.enqueueUser(request.userId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/dequeue")
    public ResponseEntity<Void> dequeue() {
        tokenQueueUseCase.dequeueUser();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/rank/{userId}")
    public ResponseEntity<Integer> getRank(@PathVariable String userId) {
        return ResponseEntity.ok(tokenQueueUseCase.getUserRank(userId));
    }

    @GetMapping("/length")
    public ResponseEntity<Integer> getLength() {
        return ResponseEntity.ok(tokenQueueUseCase.getQueueLength());
    }

    @GetMapping("/next")
    public ResponseEntity<String> getNextUser() {
        return ResponseEntity.ok(tokenQueueUseCase.getNextUser());
    }
}
