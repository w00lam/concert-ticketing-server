package kr.hhplus.be.server.tokenqueue.application.port.in;
/**
 * Defines the input port for the token queue use case.
 */

public interface TokenQueueUseCase {
    void enqueueUser(String userId);
    void dequeueUser();
    Integer getUserRank(String userId);
    Integer getQueueLength();
    String getNextUser();
}
