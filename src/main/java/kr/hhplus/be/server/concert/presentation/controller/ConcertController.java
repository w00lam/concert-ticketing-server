package kr.hhplus.be.server.concert.presentation.controller;

import kr.hhplus.be.server.concert.application.port.in.concertdate.GetConcertDatesQuery;
import kr.hhplus.be.server.concert.application.port.in.concertdate.GetConcertDatesUseCase;
import kr.hhplus.be.server.concert.application.port.in.seat.GetSeatsQuery;
import kr.hhplus.be.server.concert.application.port.in.seat.GetSeatsUseCase;
import kr.hhplus.be.server.concert.application.service.GetConcertRankingService;
import kr.hhplus.be.server.concert.presentation.dto.ConcertDateResponse;
import kr.hhplus.be.server.concert.presentation.dto.ConcertRankingResponse;
import kr.hhplus.be.server.concert.presentation.dto.SeatResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/concerts")
@AllArgsConstructor
public class ConcertController {
    private final GetConcertDatesUseCase getConcertDatesUseCase;
    private final GetSeatsUseCase getSeatsUseCase;
    private final GetConcertRankingService rankingService;

    @GetMapping("/{concertId}/dates")
    public ResponseEntity<List<ConcertDateResponse>> getConcertDates(@PathVariable UUID concertId) {
        var results = getConcertDatesUseCase.execute(new GetConcertDatesQuery(concertId));
        var response = results.stream().map(ConcertDateResponse::from).toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/dates/{concertDateId}/seats")
    public ResponseEntity<List<SeatResponse>> getSeatsByConcertDate(@PathVariable UUID concertDateId) {
        var results = getSeatsUseCase.execute(new GetSeatsQuery(concertDateId));
        var response = results.stream().map(SeatResponse::from).toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/concerts/rankings")
    public ConcertRankingResponse rankings(
            @RequestParam(defaultValue = "10") int limit
    ) {
        return ConcertRankingResponse.from(
                rankingService.getTopRankings(limit)
        );
    }
}
