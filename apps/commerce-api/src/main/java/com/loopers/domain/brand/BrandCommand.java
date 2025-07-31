package com.loopers.domain.brand;

import java.util.List;

public class BrandCommand {
    public record Create(
            String name,
            String description,
            List<BrandImageCommand.Create> images
    ) {
    }
}
