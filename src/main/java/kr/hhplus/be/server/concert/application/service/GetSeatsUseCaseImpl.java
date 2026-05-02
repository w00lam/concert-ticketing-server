package kr.hhplus.be.server.concert.application.service;

import kr.hhplus.be.server.concert.application.port.in.seat.GetSeatsQuery;
import kr.hhplus.be.server.concert.application.port.in.seat.GetSeatsResult;
import kr.hhplus.be.server.concert.application.port.in.seat.GetSeatsUseCase;
import kr.hhplus.be.server.concert.application.port.out.SeatRepositoryPort;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetSeatsUseCaseImpl implements GetSeatsUseCase {
    private final SeatRepositoryPort seatRepositoryPort;

    @Override
    @Cacheable(value = "seats", key = "#query.concertDateId")
    public List<GetSeatsResult> execute(GetSeatsQuery query) {
        return seatRepositoryPort.findSeatsByConcertDateId(query.concertDateId())
                .stream()
                .map(seat -> new GetSeatsResult(
                        seat.getId(),
                        seat.getSection(),
                        seat.getRow(),
                        seat.getNumber(),
                        seat.getGrade()
                ))
                .toList();
    }
}
