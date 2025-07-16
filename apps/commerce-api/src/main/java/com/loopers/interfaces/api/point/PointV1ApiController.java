package com.loopers.interfaces.api.point;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/points")
public class PointV1ApiController implements PointV1ApiSpec {

}
