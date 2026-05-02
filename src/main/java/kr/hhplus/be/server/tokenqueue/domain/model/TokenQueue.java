package kr.hhplus.be.server.tokenqueue.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenQueue {
    private final String userId;
    private final long joinTimestamp; // Epoch milliseconds
}
