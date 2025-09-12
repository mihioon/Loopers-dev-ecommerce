package com.loopers.interfaces.api.ranking;

import com.loopers.interfaces.api.ApiResponse;
import org.springframework.web.bind.annotation.ModelAttribute;

public interface RankingV1ApiSpec {
    ApiResponse<GetRankings.V1.Response> getRankings(@ModelAttribute GetRankings.V1.Request request);
}
