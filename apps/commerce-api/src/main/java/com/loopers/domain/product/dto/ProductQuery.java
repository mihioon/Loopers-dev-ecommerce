package com.loopers.domain.product.dto;

public class ProductQuery {
    public record Summery (
            String category,
            Long brandId,
            SortType sortType,
            int page,
            int size
    ){
        public enum SortType {
            LATEST("createdAt", "desc"),
            PRICE_ASC("price", "asc"),
            PRICE_DESC("price", "desc"),
            LIKES_DESC("likeCount", "desc");

            private final String field;
            private final String direction;

            SortType(String field, String direction) {
                this.field = field;
                this.direction = direction;
            }

            public String getField() { return field; }
            public String getDirection() { return direction; }

            public static SortType from(String sortTypeStr) {
                if (sortTypeStr == null || sortTypeStr.trim().isEmpty()) {
                    return LATEST;
                }

                return switch (sortTypeStr.toLowerCase()) {
                    case "latest" -> LATEST;
                    case "price_asc" -> PRICE_ASC;
                    case "price_desc" -> PRICE_DESC;
                    case "likes_desc" -> LIKES_DESC;
                    default -> LATEST;
                };
            }
        }
    }
}
