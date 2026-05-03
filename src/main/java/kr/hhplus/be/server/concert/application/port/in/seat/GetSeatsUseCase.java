package kr.hhplus.be.server.concert.application.port.in.seat;

import java.util.List;

public interface GetSeatsUseCase {
    List<GetSeatsResult> execute(GetSeatsQuery query);
}
