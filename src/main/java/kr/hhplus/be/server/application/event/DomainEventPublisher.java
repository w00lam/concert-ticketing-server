package kr.hhplus.be.server.application.event;
/**
 * Publishes domain events to the application event pipeline.
 */

public interface  DomainEventPublisher {
    void publish(Object event);
}
