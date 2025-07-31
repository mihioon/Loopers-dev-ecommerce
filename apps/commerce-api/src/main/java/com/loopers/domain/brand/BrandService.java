package com.loopers.domain.brand;

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
    private final BrandImageRepository brandImageRepository;

    @Transactional(rollbackFor = Exception.class)
    public BrandInfo create(final BrandCommand.Create command) {
        if(brandRepository.existsByName(command.name())) {
            throw new CoreException(ErrorType.CONFLICT, "이미 존재하는 브랜드명입니다.");
        }

        final Brand brand = brandRepository.save(new Brand(
                command.name(),
                command.description()
        ));

        List<BrandImageInfo> imageInfos = brandImageRepository.saveAll(
                command.images().stream()
                        .map(image -> new BrandImage(
                                brand.getId(),
                                image.imageUrl(),
                                image.imageType()
                        ))
                        .collect(Collectors.toList())
        ).stream()
         .map(BrandImageInfo::from)
         .collect(Collectors.toList());

        return BrandInfo.from(brand, imageInfos);
    }

    @Transactional(readOnly = true)
    public BrandInfo get(final Long brandId) {
        return brandRepository.findById(brandId)
                .map(brand -> {
                    List<BrandImageInfo> images = getBrandImages(brandId);
                    return BrandInfo.from(brand, images);
                })
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 브랜드입니다."));
    }

    @Transactional(readOnly = true)
    protected List<BrandImageInfo> getBrandImages(final Long brandId) {
        return brandImageRepository.findByBrandId(brandId)
                .stream()
                .map(BrandImageInfo::from)
                .collect(Collectors.toList());
    }
}
