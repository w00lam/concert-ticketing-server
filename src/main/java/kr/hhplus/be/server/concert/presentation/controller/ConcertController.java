package kr.hhplus.be.server.concert.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.common.presentation.ApiResponse;
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
/**
 * Handles HTTP requests for the concert feature.
 */

@RestController
@RequestMapping("/concerts")
@AllArgsConstructor
@Tag(name = "Concert", description = "콘서트 일정, 좌석, 예약 랭킹 API")
public class ConcertController {
    private final GetConcertDatesUseCase getConcertDatesUseCase;
    private final GetSeatsUseCase getSeatsUseCase;
    private final GetConcertRankingService rankingService;

    @GetMapping("/{concertId}/dates")
    @Operation(summary = "콘서트 날짜 조회", description = "콘서트 ID에 해당하는 공연 날짜 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<ConcertDateResponse>>> getConcertDates(
            @Parameter(description = "콘서트 ID", required = true) @PathVariable UUID concertId
    ) {
        var results = getConcertDatesUseCase.execute(new GetConcertDatesQuery(concertId));
        var response = results.stream().map(ConcertDateResponse::from).toList();
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/dates/{concertDateId}/seats")
    @Operation(summary = "공연 날짜별 좌석 조회", description = "공연 날짜 ID에 해당하는 좌석 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<SeatResponse>>> getSeatsByConcertDate(
            @Parameter(description = "공연 날짜 ID", required = true) @PathVariable UUID concertDateId
    ) {
        var results = getSeatsUseCase.execute(new GetSeatsQuery(concertDateId));
        var response = results.stream().map(SeatResponse::from).toList();

        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/rankings")
    @Operation(summary = "콘서트 예약 랭킹 조회", description = "예약 확정/취소 이벤트를 기반으로 집계된 콘서트 랭킹을 조회합니다.")
    public ResponseEntity<ApiResponse<ConcertRankingResponse>> rankings(
            @Parameter(description = "조회할 랭킹 개수") @RequestParam(defaultValue = "10") int limit
    ) {
        var response = ConcertRankingResponse.from(rankingService.getTopRankings(limit));

        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
