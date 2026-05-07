package kr.hhplus.be.server.tokenqueue.application.service;

import kr.hhplus.be.server.tokenqueue.application.port.in.TokenQueueUseCase;
import kr.hhplus.be.server.tokenqueue.application.port.out.TokenQueueRepositoryPort;
import kr.hhplus.be.server.tokenqueue.domain.model.TokenQueue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
/**
 * Implements the token queue use case and coordinates transactional work.
 */

@Service
@RequiredArgsConstructor
public class TokenQueueUseCaseImpl implements TokenQueueUseCase {
    private final TokenQueueRepositoryPort tokenQueueRepository;
    private final Clock clock;

    @Override
    public void enqueueUser(String userId) {
        TokenQueue tokenQueue = new TokenQueue(userId, Instant.now(clock).toEpochMilli());
        tokenQueueRepository.addUser(tokenQueue);
    }

    @Override
    public void dequeueUser() {
        // Redis pops the first waiting user atomically, preventing duplicate admission across app instances.
        tokenQueueRepository.popNextUser();
    }

    @Override
    public Integer getUserRank(String userId) {
        return tokenQueueRepository.getUserRank(userId);
    }

    @Override
    public Integer getQueueLength() {
        return tokenQueueRepository.getQueueLength();
    }

    @Override
    public String getNextUser() {
        return tokenQueueRepository.getNextUser();
    }
}
