package kr.hhplus.be.server.tokenqueue.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public record TokenQueueRequest(
        @NotBlank String userId
) {
}

