package com.findit.repository.specification;

import com.findit.dto.item.ItemSearchFilter;
import com.findit.entity.FoundItem;
import com.findit.entity.LostItem;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public final class ItemSpecifications {

    private ItemSpecifications() {}

    public static Specification<LostItem> filterLostItems(ItemSearchFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getCategory() != null) {
                predicates.add(cb.equal(root.get("category"), filter.getCategory()));
            }

            if (filter.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), filter.getStatus()));
            }

            if (StringUtils.hasText(filter.getLocation())) {
                predicates.add(cb.like(cb.lower(root.get("locationLost")), "%" + filter.getLocation().toLowerCase() + "%"));
            }

            if (filter.getStartDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("dateLost"), filter.getStartDate()));
            }

            if (filter.getEndDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("dateLost"), filter.getEndDate()));
            }

            if (StringUtils.hasText(filter.getQuery())) {
                String pattern = "%" + filter.getQuery().trim().toLowerCase() + "%";
                Predicate titleMatch = cb.like(cb.lower(root.get("title")), pattern);
                Predicate descMatch = cb.like(cb.lower(root.get("description")), pattern);
                Predicate brandMatch = cb.like(cb.lower(root.get("brand")), pattern);
                Predicate colorMatch = cb.like(cb.lower(root.get("primaryColor")), pattern);
                predicates.add(cb.or(titleMatch, descMatch, brandMatch, colorMatch));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<FoundItem> filterFoundItems(ItemSearchFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getCategory() != null) {
                predicates.add(cb.equal(root.get("category"), filter.getCategory()));
            }

            if (filter.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), filter.getStatus()));
            }

            if (StringUtils.hasText(filter.getLocation())) {
                predicates.add(cb.like(cb.lower(root.get("locationFound")), "%" + filter.getLocation().toLowerCase() + "%"));
            }

            if (filter.getStartDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("dateFound"), filter.getStartDate()));
            }

            if (filter.getEndDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("dateFound"), filter.getEndDate()));
            }

            if (StringUtils.hasText(filter.getQuery())) {
                String pattern = "%" + filter.getQuery().trim().toLowerCase() + "%";
                Predicate titleMatch = cb.like(cb.lower(root.get("title")), pattern);
                Predicate descMatch = cb.like(cb.lower(root.get("publicDescription")), pattern);
                Predicate brandMatch = cb.like(cb.lower(root.get("brand")), pattern);
                Predicate colorMatch = cb.like(cb.lower(root.get("primaryColor")), pattern);
                predicates.add(cb.or(titleMatch, descMatch, brandMatch, colorMatch));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
