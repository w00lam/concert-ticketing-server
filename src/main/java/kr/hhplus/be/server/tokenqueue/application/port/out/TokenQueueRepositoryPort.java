package kr.hhplus.be.server.tokenqueue.application.port.out;

import kr.hhplus.be.server.tokenqueue.domain.model.TokenQueue;
/**
 * Defines the output port for token queue persistence.
 */

public interface TokenQueueRepositoryPort {
    void addUser(TokenQueue tokenQueue);
    void removeUser(String userId);
    Integer getUserRank(String userId);
    Integer getQueueLength();
    String getNextUser();
    String popNextUser();
}
