package com.loopers.domain.brand;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class BrandService {
    public static final Long DEFAULT_BRAND_ID = 0L;

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

        command.images()
                .forEach(image -> brand.addImage(new BrandImage(
                        image.imageUrl(),
                        image.imageType()
                )));

        return BrandInfo.from(brand);
    }

    @Transactional(readOnly = true)
    public BrandInfo getBy(final Long brandId) {
        Brand brand = brandRepository.findWithImagesById(brandId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 브랜드입니다."));
        return BrandInfo.from(brand);

    }

    public void validateBrandId(Long brandId) {
        if (brandId == null || DEFAULT_BRAND_ID.equals(brandId)) {
            throw new CoreException(ErrorType.BAD_REQUEST, "유효하지 않은 브랜드 ID입니다.");
        }
    }
}
