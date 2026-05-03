package kr.hhplus.be.server.concert.application.port.in.seat;

import java.util.List;
/**
 * Defines the input port for the concert use case.
 */

public interface GetSeatsUseCase {
    List<GetSeatsResult> execute(GetSeatsQuery query);
}
