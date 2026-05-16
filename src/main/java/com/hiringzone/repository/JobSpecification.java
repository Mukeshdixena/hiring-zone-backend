package com.hiringzone.repository;

import com.hiringzone.model.Job;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;

public class JobSpecification {

    public static Specification<Job> active() {
        return (root, query, cb) -> cb.and(
                cb.isFalse(root.get("flagged")),
                cb.isFalse(root.get("expired"))
        );
    }

    public static Specification<Job> keyword(String keyword) {
        if (keyword == null || keyword.isBlank()) return null;
        String pattern = "%" + keyword.toLowerCase() + "%";
        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("title")), pattern),
                cb.like(cb.lower(root.join("company", JoinType.INNER).get("name")), pattern)
        );
    }

    public static Specification<Job> location(String location) {
        if (location == null || location.isBlank()) return null;
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("location")), "%" + location.toLowerCase() + "%");
    }

    public static Specification<Job> category(String category) {
        if (category == null || category.isBlank()) return null;
        return (root, query, cb) -> cb.equal(root.get("category"), category);
    }

    public static Specification<Job> types(List<String> types) {
        if (types == null || types.isEmpty()) return null;
        return (root, query, cb) -> root.get("type").in(types);
    }

    public static Specification<Job> experienceLevels(List<String> levels) {
        if (levels == null || levels.isEmpty()) return null;
        return (root, query, cb) -> root.get("experienceLevel").in(levels);
    }

    public static Specification<Job> minSalary(BigDecimal min) {
        if (min == null) return null;
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("salaryMin"), min);
    }
}
