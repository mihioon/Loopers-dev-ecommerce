package com.loopers.domain.product.dto;

import com.loopers.domain.product.Product;

import java.math.BigDecimal;
import java.util.List;

public class ProductCommand {
    public record Create(
            String name,
            String description, 
            BigDecimal price,
            String category,
            Long brandId,
            Integer initialStock,
            List<ImageDto> images,
            DetailDto detail
    ) {}

    public record ImageDto(
            String imageUrl,
            Product.ImageType imageType
    ) {}

    public record DetailDto(
            String detailContent,
            String specifications,
            String materialInfo,
            String careInstructions
    ) {}


}
