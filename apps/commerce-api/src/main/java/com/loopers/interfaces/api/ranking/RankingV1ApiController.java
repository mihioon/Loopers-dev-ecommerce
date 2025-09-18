package com.loopers.interfaces.api.ranking;

import com.loopers.application.ranking.RankingCriteria;
import com.loopers.application.ranking.RankingFacade;
import com.loopers.application.ranking.RankingResult;
import com.loopers.interfaces.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/rankings")
public class RankingV1ApiController implements RankingV1ApiSpec {

    private final RankingFacade rankingFacade;

    @Override
    @GetMapping
    public ApiResponse<GetRankings.V1.Response> getRankings(
            @ModelAttribute GetRankings.V1.Request request
    ) {
        log.debug("Get rankings request: {}", request);
        
        RankingCriteria.Query criteria = request.toCriteria();
        RankingResult.Query result = rankingFacade.getRankings(criteria);
        GetRankings.V1.Response response = GetRankings.V1.Response.from(result);
        
        log.debug("Get rankings response: {} items, page {}/{}", 
                 response.rankings().size(), response.currentPage(), response.totalPages());
        
        return ApiResponse.success(response);
    }
}
