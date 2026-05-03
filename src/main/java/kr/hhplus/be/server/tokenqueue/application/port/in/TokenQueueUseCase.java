package kr.hhplus.be.server.tokenqueue.application.port.in;

public interface TokenQueueUseCase {
    void enqueueUser(String userId);
    void dequeueUser();
    Integer getUserRank(String userId);
    Integer getQueueLength();
    String getNextUser();
}
