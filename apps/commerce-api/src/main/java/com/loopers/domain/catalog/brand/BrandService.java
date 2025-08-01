package com.loopers.domain.catalog.brand;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class BrandService {
    private final BrandRepository brandRepository;

    @Transactional(rollbackFor = Exception.class)
    public BrandInfo create(final BrandCommand.Create command) {
        if(brandRepository.existsByName(command.name())) {
            throw new CoreException(ErrorType.CONFLICT, "이미 존재하는 브랜드명입니다.");
        }

        final Brand brand = brandRepository.save(new Brand(
                command.name(),
                command.description()
        ));

        List<BrandInfo.BrandImageInfo> imageInfos = brandRepository.saveAll(
                command.images().stream()
                        .map(image -> new Brand.BrandImage(
                                image.imageUrl(),
                                image.imageType()
                        ))
                        .collect(Collectors.toList())
        ).stream()
         .map(BrandInfo.BrandImageInfo::from)
         .collect(Collectors.toList());

        return BrandInfo.from(brand, imageInfos);
    }

    @Transactional(readOnly = true)
    public BrandInfo get(final Long brandId) {
        return brandRepository.findById(brandId)
                .map(brand -> {
                    List<BrandInfo.BrandImageInfo> images = getBrandImages(brandId);
                    return BrandInfo.from(brand, images);
                })
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 브랜드입니다."));
    }

    @Transactional(readOnly = true)
    protected List<BrandInfo.BrandImageInfo> getBrandImages(final Long brandId) {
        return brandRepository.findBrandImagesByBrandId(brandId)
                .stream()
                .map(BrandInfo.BrandImageInfo::from)
                .collect(Collectors.toList());
    }
}
