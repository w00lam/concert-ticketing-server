package kr.hhplus.be.server.tokenqueue.presentation.controller;

import jakarta.validation.Valid;
import kr.hhplus.be.server.common.presentation.ApiResponse;
import kr.hhplus.be.server.tokenqueue.application.port.in.TokenQueueUseCase;
import kr.hhplus.be.server.tokenqueue.presentation.dto.TokenQueueRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/queue/token")
public class TokenQueueController {
    private final TokenQueueUseCase tokenQueueUseCase;

    @PostMapping("/enqueue")
    public ResponseEntity<ApiResponse<Void>> enqueue(@Valid @RequestBody TokenQueueRequest request) {
        tokenQueueUseCase.enqueueUser(request.userId());

        return ResponseEntity.ok(ApiResponse.ok());
    }

    @PostMapping("/dequeue")
    public ResponseEntity<ApiResponse<Void>> dequeue() {
        tokenQueueUseCase.dequeueUser();

        return ResponseEntity.ok(ApiResponse.ok());
    }

    @GetMapping("/rank/{userId}")
    public ResponseEntity<ApiResponse<Integer>> getRank(@PathVariable String userId) {
        return ResponseEntity.ok(ApiResponse.ok(tokenQueueUseCase.getUserRank(userId)));
    }

    @GetMapping("/length")
    public ResponseEntity<ApiResponse<Integer>> getLength() {
        return ResponseEntity.ok(ApiResponse.ok(tokenQueueUseCase.getQueueLength()));
    }

    @GetMapping("/next")
    public ResponseEntity<ApiResponse<String>> getNextUser() {
        return ResponseEntity.ok(ApiResponse.ok(tokenQueueUseCase.getNextUser()));
    }
}
