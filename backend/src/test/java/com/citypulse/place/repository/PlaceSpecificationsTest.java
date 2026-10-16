package com.citypulse.place.repository;

import com.citypulse.place.entity.Place;
import com.citypulse.place.entity.PlaceStatus;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PlaceSpecificationsTest {

    @Mock
    private Root<Place> root;

    @Mock
    private CriteriaQuery<?> query;

    @Mock
    private CriteriaBuilder cb;

    @Test
    @DisplayName("Should build specification with only published status when filters are null")
    void shouldBuildSpecWithDefaultStatusOnly() {
        Path<Object> statusPath = mock(Path.class);
        Predicate statusPredicate = mock(Predicate.class);
        Predicate andPredicate = mock(Predicate.class);

        when(root.get("status")).thenReturn(statusPath);
        when(cb.equal(statusPath, PlaceStatus.PUBLISHED)).thenReturn(statusPredicate);
        when(cb.and(any(Predicate[].class))).thenReturn(andPredicate);

        Specification<Place> spec = PlaceSpecifications.withFilters(
            null, null, null, null, null, null, PlaceStatus.PUBLISHED
        );

        Predicate result = spec.toPredicate(root, query, cb);

        assertThat(result).isNotNull();
        verify(cb).equal(statusPath, PlaceStatus.PUBLISHED);
    }

    @Test
    @DisplayName("Should build specification with search keyword, category, district, price, and rating")
    void shouldBuildSpecWithAllFilters() {
        Path<Object> statusPath = mock(Path.class);
        Path<String> namePath = mock(Path.class);
        Path<String> slugPath = mock(Path.class);
        Path<String> catPath = mock(Path.class);
        Path<String> distPath = mock(Path.class);
        Path<String> tagsPath = mock(Path.class);
        Path<String> descPath = mock(Path.class);
        Path<BigDecimal> priceMinPath = mock(Path.class);
        Path<BigDecimal> priceMaxPath = mock(Path.class);
        Path<BigDecimal> ratingPath = mock(Path.class);

        Expression<String> lowerExpr = mock(Expression.class);
        Expression<String> coalesceExpr = mock(Expression.class);
        Predicate dummyPredicate = mock(Predicate.class);

        when(root.get("status")).thenReturn(statusPath);
        when(root.get("name")).thenReturn((Path) namePath);
        when(root.get("slug")).thenReturn((Path) slugPath);
        when(root.get("category")).thenReturn((Path) catPath);
        when(root.get("district")).thenReturn((Path) distPath);
        when(root.get("tags")).thenReturn((Path) tagsPath);
        when(root.get("shortDescription")).thenReturn((Path) descPath);
        when(root.get("priceMin")).thenReturn((Path) priceMinPath);
        when(root.get("priceMax")).thenReturn((Path) priceMaxPath);
        when(root.get("ratingAverage")).thenReturn((Path) ratingPath);

        when(cb.equal(statusPath, PlaceStatus.PUBLISHED)).thenReturn(dummyPredicate);
        when(cb.lower(any())).thenReturn(lowerExpr);
        when(cb.coalesce(any(), anyString())).thenReturn((Expression) coalesceExpr);
        when(cb.like(any(), anyString())).thenReturn(dummyPredicate);
        when(cb.or(any(Predicate[].class))).thenReturn(dummyPredicate);
        when(cb.and(any(Predicate[].class))).thenReturn(dummyPredicate);
        when(cb.isNotNull(any())).thenReturn(dummyPredicate);
        when(cb.isNull(any())).thenReturn(dummyPredicate);
        when(cb.greaterThanOrEqualTo(any(Expression.class), any(BigDecimal.class))).thenReturn(dummyPredicate);
        when(cb.lessThanOrEqualTo(any(Expression.class), any(BigDecimal.class))).thenReturn(dummyPredicate);

        Specification<Place> spec = PlaceSpecifications.withFilters(
            "Mỹ Khê", "BEACH", "Sơn Trà", BigDecimal.valueOf(0), BigDecimal.valueOf(100000), 4.5, PlaceStatus.PUBLISHED
        );

        Predicate result = spec.toPredicate(root, query, cb);

        assertThat(result).isNotNull();
    }
}
