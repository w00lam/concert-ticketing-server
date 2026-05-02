package kr.hhplus.be.server.tokenqueue.domain.service;

import kr.hhplus.be.server.tokenqueue.domain.model.TokenQueue;

public class TokenQueueDomainService {
    // FIFO 기반으로 순서 비교
    public boolean isEarlier(TokenQueue a, TokenQueue b) {
        return a.getJoinTimestamp() < b.getJoinTimestamp();
    }
}
