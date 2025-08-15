package com.loopers.infrastructure.product;

import com.loopers.domain.product.Product;
import com.loopers.domain.product.dto.ProductWithLikeCountProjection;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;
import java.util.stream.Stream;

import static com.loopers.domain.product.QProduct.product;
import static com.loopers.domain.product.QProductStatus.productStatus;

@RequiredArgsConstructor
public class ProductJpaRepositoryImpl implements ProductJpaRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ProductWithLikeCountProjection> findProductsWithFilter(
            String category,
            Long brandId,
            Pageable pageable) {
        
        List<ProductWithLikeCountProjection> results = queryFactory
                .select(product, productStatus.likeCount)
                .from(product)
                .innerJoin(productStatus).on(productStatus.productId.eq(product.id))
                .where(
                        categoryEq(category),
                        brandIdEq(brandId)
                )
                .orderBy(getOrderSpecifiers(pageable))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch()
                .stream()
                .map(tuple -> new ProductWithLikeCountProjection() {
                    @Override
                    public Product getProduct() {
                        return tuple.get(product);
                    }

                    @Override
                    public Long getLikeCount() {
                        Integer count = tuple.get(productStatus.likeCount);
                        return count != null ? count.longValue() : 0L;
                    }
                })
                .collect(java.util.stream.Collectors.toList());

        JPAQuery<Long> countQuery = queryFactory
                .select(product.count())
                .from(product)
                .innerJoin(productStatus).on(productStatus.productId.eq(product.id))
                .where(
                        categoryEq(category),
                        brandIdEq(brandId)
                );

        return PageableExecutionUtils.getPage(results, pageable, countQuery::fetchOne);
    }

    @Override
    public Page<ProductWithLikeCountProjection> findProductsWithFilterByLikes(
            String category,
            Long brandId,
            Pageable pageable) {
        
        List<ProductWithLikeCountProjection> results = queryFactory
                .select(product, productStatus.likeCount)
                .from(product)
                .innerJoin(productStatus).on(productStatus.productId.eq(product.id))
                .where(
                        categoryEq(category),
                        brandIdEq(brandId)
                )
                .orderBy(getLikeCountOrderSpecifiers(pageable))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch()
                .stream()
                .map(tuple -> new ProductWithLikeCountProjection() {
                    @Override
                    public Product getProduct() {
                        return tuple.get(product);
                    }

                    @Override
                    public Long getLikeCount() {
                        Integer count = tuple.get(productStatus.likeCount);
                        return count != null ? count.longValue() : 0L;
                    }
                })
                .collect(java.util.stream.Collectors.toList());

        JPAQuery<Long> countQuery = queryFactory
                .select(product.count())
                .from(product)
                .innerJoin(productStatus).on(productStatus.productId.eq(product.id))
                .where(
                        categoryEq(category),
                        brandIdEq(brandId)
                );

        return PageableExecutionUtils.getPage(results, pageable, countQuery::fetchOne);
    }

    @Override
    public long countProductsWithFilter(String category, Long brandId) {
        Long count = queryFactory
                .select(product.count())
                .from(product)
                .where(
                        categoryEq(category),
                        brandIdEq(brandId)
                )
        .fetchOne();

        return count != null ? count : 0L;
    }

    private BooleanExpression categoryEq(String category) {
        return category != null ? product.category.eq(category) : null;
    }

    private BooleanExpression brandIdEq(Long brandId) {
        return brandId != null ? product.brandId.eq(brandId) : null;
    }
    
    private OrderSpecifier<?>[] getOrderSpecifiers(Pageable pageable) {
        if (pageable.getSort().isEmpty()) {
            return new OrderSpecifier[]{product.id.desc()};
        }
        
        return pageable.getSort().stream()
                .flatMap(order -> {
                    String property = order.getProperty();
                    return switch (property) {
                        case "createdAt" -> order.isAscending() 
                            ? Stream.of(product.createdAt.asc(), product.id.asc())
                            : Stream.of(product.createdAt.desc(), product.id.desc());
                        case "price" -> order.isAscending() 
                            ? Stream.of(product.price.asc(), product.id.asc())
                            : Stream.of(product.price.desc(), product.id.desc());
                        case "id" -> order.isAscending() 
                            ? Stream.of(product.id.asc())
                            : Stream.of(product.id.desc());
                        default -> Stream.of(product.id.desc());
                    };
                })
                .toArray(OrderSpecifier[]::new);
    }
    
    private OrderSpecifier<?>[] getLikeCountOrderSpecifiers(Pageable pageable) {
        if (pageable.getSort().isEmpty()) {
            return new OrderSpecifier[]{productStatus.likeCount.desc(), productStatus.productId.desc()};
        }
        
        return pageable.getSort().stream()
                .flatMap(order -> {
                    String property = order.getProperty();
                    return switch (property) {
                        case "likeCount" -> order.isAscending()
                            ? Stream.of(productStatus.likeCount.asc(), productStatus.productId.asc())
                            : Stream.of(productStatus.likeCount.desc(), productStatus.productId.desc());
                        default -> Stream.of(productStatus.likeCount.desc(), productStatus.productId.desc());
                    };
                })
                .toArray(OrderSpecifier[]::new);
    }
}
