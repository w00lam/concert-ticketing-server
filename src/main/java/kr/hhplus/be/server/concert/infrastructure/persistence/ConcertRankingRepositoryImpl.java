package kr.hhplus.be.server.concert.infrastructure.persistence;

import kr.hhplus.be.server.concert.application.port.out.ConcertRankingRepositoryPort;
import kr.hhplus.be.server.concert.application.service.ConcertRankingItem;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ConcertRankingRepositoryImpl implements ConcertRankingRepositoryPort {
    private static final String RANKING_KEY = "concert:ranking";

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void increase(UUID concertId, long delta) {
        redisTemplate.opsForZSet()
                .incrementScore(RANKING_KEY, concertId.toString(), delta);
    }

    @Override
    public void decrease(UUID concertId, long delta) {
        redisTemplate.opsForZSet()
                .incrementScore(RANKING_KEY, concertId.toString(), -delta);
    }

    @Override
    public List<ConcertRankingItem> findTopRanked(int limit) {
        ZSetOperations<String, String> zSetOps = redisTemplate.opsForZSet();

        Set<ZSetOperations.TypedTuple<String>> results = zSetOps.reverseRangeWithScores(RANKING_KEY, 0, limit - 1);

        List<ConcertRankingItem> items = new ArrayList<>();
        if (results == null) {
            return items;
        }

        for (ZSetOperations.TypedTuple<String> tuple : results) {
            UUID concertId = UUID.fromString(tuple.getValue());
            long reservationCount = tuple.getScore().longValue();
            items.add(new ConcertRankingItem(concertId, reservationCount));
        }

        return items;
    }
}
