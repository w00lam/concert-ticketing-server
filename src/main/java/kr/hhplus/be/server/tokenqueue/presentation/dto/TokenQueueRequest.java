package kr.hhplus.be.server.tokenqueue.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "대기열 등록 요청")
public record TokenQueueRequest(
        @Schema(description = "사용자 ID", example = "user-1", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank String userId
) {
}

