package kr.hhplus.be.server.reservation.presentation.controller;

import kr.hhplus.be.server.common.exception.ClientInputException;
import kr.hhplus.be.server.common.exception.ErrorCode;
import kr.hhplus.be.server.reservation.application.port.in.ConfirmReservationCommand;
import kr.hhplus.be.server.reservation.application.port.in.ConfirmReservationUseCase;
import kr.hhplus.be.server.reservation.application.port.in.MakeReservationCommand;
import kr.hhplus.be.server.reservation.application.port.in.MakeReservationUseCase;
import kr.hhplus.be.server.reservation.presentation.dto.ConfirmReservationResponse;
import kr.hhplus.be.server.reservation.presentation.dto.MakeReservationRequest;
import kr.hhplus.be.server.reservation.presentation.dto.MakeReservationResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/reservations")
@AllArgsConstructor
public class ReservationController {
    private final MakeReservationUseCase makeReservationUseCase;
    private final ConfirmReservationUseCase confirmReservationUseCase;

    @PostMapping
    public ResponseEntity<MakeReservationResponse> makeReservation(@RequestBody MakeReservationRequest request) {
        validateMakeReservationRequest(request);
        var result = makeReservationUseCase.execute(new MakeReservationCommand(request.userId(), request.concertId(), request.seatId()));
        var response = MakeReservationResponse.from(result);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{reservationId}/confirm")
    public ResponseEntity<ConfirmReservationResponse> confirmReservation(@PathVariable UUID reservationId) {
        var result = confirmReservationUseCase.execute(new ConfirmReservationCommand(reservationId));
        var response = ConfirmReservationResponse.from(result);

        return ResponseEntity.ok(response);
    }

    private void validateMakeReservationRequest(MakeReservationRequest request) {
        // Keep HTTP boundary validation explicit before delegating to the use case.
        if (request == null) throw new ClientInputException(ErrorCode.REQUEST_BODY_REQUIRED, "요청 본문은 필수입니다.");
        if (request.userId() == null) throw new ClientInputException(ErrorCode.USER_ID_REQUIRED, "사용자 ID는 필수입니다.");
        if (request.concertId() == null) throw new ClientInputException(ErrorCode.CONCERT_ID_REQUIRED, "콘서트 ID는 필수입니다.");
        if (request.seatId() == null) throw new ClientInputException(ErrorCode.SEAT_ID_REQUIRED, "좌석 ID는 필수입니다.");
    }
}
