package com.citypulse.place.repository;

import com.citypulse.place.entity.Place;
import com.citypulse.place.entity.PlaceStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Reusable JPA Specifications for filtering and searching places.
 */
public final class PlaceSpecifications {

    private PlaceSpecifications() {
    }

    public static Specification<Place> withFilters(
            String q,
            String category,
            String district,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Double minRating,
            PlaceStatus status
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. Status predicate (default to PUBLISHED for public access)
            PlaceStatus targetStatus = status != null ? status : PlaceStatus.PUBLISHED;
            predicates.add(cb.equal(root.get("status"), targetStatus));

            // 2. Keyword search (q): name, slug, category, district, tags, shortDescription
            if (q != null && !q.trim().isBlank()) {
                String keywordPattern = "%" + q.trim().toLowerCase() + "%";
                Predicate nameMatch = cb.like(cb.lower(root.get("name")), keywordPattern);
                Predicate slugMatch = cb.like(cb.lower(root.get("slug")), keywordPattern);
                Predicate catMatch = cb.like(cb.lower(root.get("category")), keywordPattern);
                Predicate districtMatch = cb.like(cb.lower(root.get("district")), keywordPattern);
                Predicate tagsMatch = cb.like(cb.lower(cb.coalesce(root.get("tags"), "")), keywordPattern);
                Predicate descMatch = cb.like(cb.lower(cb.coalesce(root.get("shortDescription"), "")), keywordPattern);

                predicates.add(cb.or(nameMatch, slugMatch, catMatch, districtMatch, tagsMatch, descMatch));
            }

            // 3. Category filter (case-insensitive substring or match)
            if (category != null && !category.trim().isBlank()) {
                String catPattern = "%" + category.trim().toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get("category")), catPattern));
            }

            // 4. District filter (case-insensitive substring)
            if (district != null && !district.trim().isBlank()) {
                String distPattern = "%" + district.trim().toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get("district")), distPattern));
            }

            // 5. Price range filter
            if (minPrice != null) {
                // Must have priceMax >= minPrice, or if priceMax is null, priceMin >= minPrice
                Predicate priceMaxGte = cb.and(
                    cb.isNotNull(root.get("priceMax")),
                    cb.greaterThanOrEqualTo(root.get("priceMax"), minPrice)
                );
                Predicate priceMinGte = cb.and(
                    cb.isNull(root.get("priceMax")),
                    cb.isNotNull(root.get("priceMin")),
                    cb.greaterThanOrEqualTo(root.get("priceMin"), minPrice)
                );
                predicates.add(cb.or(priceMaxGte, priceMinGte));
            }

            if (maxPrice != null) {
                // Must have priceMin <= maxPrice
                predicates.add(cb.and(
                    cb.isNotNull(root.get("priceMin")),
                    cb.lessThanOrEqualTo(root.get("priceMin"), maxPrice)
                ));
            }

            // 6. Minimum rating filter
            if (minRating != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("ratingAverage"), BigDecimal.valueOf(minRating)));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
