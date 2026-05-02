package kr.hhplus.be.server.tokenqueue.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Queue", description = "대기열 토큰 API")
public class TokenQueueController {
    private final TokenQueueUseCase tokenQueueUseCase;

    @PostMapping("/enqueue")
    @Operation(summary = "대기열 등록", description = "사용자를 대기열에 등록합니다.")
    public ResponseEntity<ApiResponse<Void>> enqueue(@Valid @RequestBody TokenQueueRequest request) {
        tokenQueueUseCase.enqueueUser(request.userId());

        return ResponseEntity.ok(ApiResponse.ok());
    }

    @PostMapping("/dequeue")
    @Operation(summary = "대기열 다음 사용자 제거", description = "대기열에서 가장 앞선 사용자를 제거합니다.")
    public ResponseEntity<ApiResponse<Void>> dequeue() {
        tokenQueueUseCase.dequeueUser();

        return ResponseEntity.ok(ApiResponse.ok());
    }

    @GetMapping("/rank/{userId}")
    @Operation(summary = "대기 순번 조회", description = "사용자의 현재 대기 순번을 조회합니다.")
    public ResponseEntity<ApiResponse<Integer>> getRank(
            @Parameter(description = "사용자 ID", required = true) @PathVariable String userId
    ) {
        return ResponseEntity.ok(ApiResponse.ok(tokenQueueUseCase.getUserRank(userId)));
    }

    @GetMapping("/length")
    @Operation(summary = "대기열 길이 조회", description = "현재 대기열에 등록된 사용자 수를 조회합니다.")
    public ResponseEntity<ApiResponse<Integer>> getLength() {
        return ResponseEntity.ok(ApiResponse.ok(tokenQueueUseCase.getQueueLength()));
    }

    @GetMapping("/next")
    @Operation(summary = "다음 입장 사용자 조회", description = "대기열에서 다음으로 입장할 사용자 ID를 조회합니다.")
    public ResponseEntity<ApiResponse<String>> getNextUser() {
        return ResponseEntity.ok(ApiResponse.ok(tokenQueueUseCase.getNextUser()));
    }
}
