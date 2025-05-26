package com.core.payment_gateway_service.DTO;

import com.core.payment_gateway_service.enums.SearchOperator;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class SpecificationHelper {
    private static final String DELIMITER_ATTRIBUTE = "\\.";

    public static <T> Specification<T> searchAttributeEquals(String attribute, Object value) {
        return (root, query, builder) -> builder.equal(root.get(attribute), value);
    }

    public static <T> Specification<T> searchBetween(String attribute, Comparable from, Comparable to) {
        return (root, query, builder) -> builder.between(root.get(attribute), from, to);
    }

    public static Specification searchAttributeContains(String attribute, String value) {
        final String finalText = value;
        return (root, query, cb) -> {
            if (!StringUtils.hasText(finalText)) {
                return null;
            }
            return cb.like(cb.lower(root.get(attribute)), contains(finalText.toLowerCase()));
        };
    }

    public static Specification searchAttributeContainsPrefix(String attribute, String value) {
        final String finalText = value;
        return (root, query, cb) -> {
            if (!StringUtils.hasText(finalText)) {
                return null;
            }
            return cb.like(root.get(attribute), containsBySuffix(finalText));
        };
    }

    private static String contains(String expression) {
        return MessageFormat.format("%{0}%", expression);
    }

    private static String containsBySuffix(String expression) {
        return MessageFormat.format("{0}%", expression);
    }

    public static Specification searchAttributeEqual(String attribute, String value) {
        final String finalText = value;
        return (root, query, cb) -> {
            if (!StringUtils.hasText(finalText)) {
                return null;
            }
            return cb.equal(root.get(attribute), finalText);
        };
    }

    public static Specification searchAttributeNotEqual(String attribute, String value) {
        final String finalText = value;
        return (root, query, cb) -> {
            if (!StringUtils.hasText(finalText)) {
                return null;
            }
            return cb.notEqual(root.get(attribute), finalText);
        };
    }

    public static Specification searchAttributeEqual(String attribute, Object value) {
        return (root, query, cb) -> {
            if (null == value) {
                return null;
            }
            return cb.equal(root.get(attribute), value);
        };
    }

    public static Specification searchAttributeIn(String attribute, List<String> values) {
        return (root, query, cb) -> {
            if (values == null || values.isEmpty()) {
                return null;
            }
            Path<String> attributePath = root.get(attribute);
            Predicate[] predicates = new Predicate[values.size()];

            for (int i = 0; i < values.size(); i++) {
                predicates[i] = cb.equal(attributePath, values.get(i));
            }

            return cb.or(predicates);
        };
    }

    public static Specification searchAttributeNotIn(String attribute, List<String> values) {
        return (root, query, cb) -> {
            if (values == null || values.isEmpty()) {
                return null;
            }
            Path<String> attributePath = root.get(attribute);
            Predicate[] predicates = new Predicate[values.size()];

            for (int i = 0; i < values.size(); i++) {
                predicates[i] = cb.notEqual(attributePath, values.get(i));
            }

            return cb.or(predicates);
        };
    }

    public static <T extends Comparable> Specification searchAttributeGreaterThan(String attribute, T value) {
        return (root, query, criteriaBuilder) -> {
            if (null == value) {
                return null;
            }
            return criteriaBuilder.greaterThan(root.get(attribute), value);
        };
    }

    public static <T extends Comparable> Specification searchAttributeLessThan(String attribute, T value) {
        return (root, query, criteriaBuilder) -> {
            if (null == value) {
                return null;
            }
            return criteriaBuilder.lessThan(root.get(attribute), value);
        };
    }

    public static <T extends Comparable> Specification searchAttributeGreaterThanOrEqualTo(String attribute, T value) {
        return (root, query, criteriaBuilder) -> {
            if (null == value) {
                return null;
            }
            return criteriaBuilder.greaterThanOrEqualTo(root.get(attribute), value);
        };
    }

    public static <T extends Comparable> Specification searchAttributeLessThanOrEqualTo(String attribute, T value) {
        return (root, query, criteriaBuilder) -> {
            if (null == value) {
                return null;
            }
            return criteriaBuilder.lessThanOrEqualTo(root.get(attribute), value);
        };
    }

    public static Specification searchAttributeEqualIgnoreCase(String attribute, String value) {
        final String finalText = value;
        return (root, query, cb) -> {
            if (!StringUtils.hasText(finalText)) {
                return null;
            }
            return cb.equal(cb.lower(root.get(attribute)), finalText.toLowerCase());
        };
    }

    public static Specification searchAttributeLike(String attribute, String value) {
        final String finalText = value;
        return (root, query, cb) -> {
            if (!StringUtils.hasText(finalText)) {
                return null;
            }
            return cb.like(root.get(attribute), "%" + finalText + "%");
        };
    }

    public static Specification searchAttributeLikeIgnoreCase(String attribute, String value) {
        final String finalText = value;
        return (root, query, cb) -> {
            if (!StringUtils.hasText(finalText)) {
                return null;
            }
            return cb.like(cb.lower(root.get(attribute)), "%" + finalText.toLowerCase() + "%");
        };
    }

    public static Specification searchAttributeNotNull(String attribute) {
        return (root, query, cb) -> cb.isNotNull(root.get(attribute));
    }

    public static Specification searchAttributeLocalDate(String attribute,
                                                         LocalDate value) {
        return (root, query, cb) -> {
            if (Objects.isNull(value)) {
                return null;
            }
            return cb.equal(root.get(attribute), value);
        };
    }

    public static Specification searchBetweenAttributeLocalDate(String attribute,
                                                                LocalDate from, LocalDate to) {
        return (root, query, cb) -> {
            if (Objects.isNull(from) || Objects.isNull(to)) {
                return null;
            }
            return cb.between(root.get(attribute), from, to);
        };
    }

    public static Specification searchBetweenAttributeLocalDateTime(String attribute,
                                                                    LocalDateTime from, LocalDateTime to) {
        return (root, query, cb) -> {
            if (Objects.isNull(from) || Objects.isNull(to)) {
                return null;
            }
            return cb.between(root.get(attribute), from, to);
        };
    }

    public static Specification searchAttributeInteger(String attribute, Integer value) {
        final Integer finalText = value;
        return (root, query, cb) -> {
            if (finalText == null) {
                return null;
            }
            return cb.equal(
                    root.get(attribute),
                    finalText
            );
        };
    }

    public static Specification searchAttributeBoolean(String attribute, Boolean value) {
        final Boolean finalText = value;
        return (root, query, cb) -> {
            if (finalText == null) {
                return null;
            }
            return cb.equal(
                    root.get(attribute),
                    finalText
            );
        };
    }

    public static Specification searchNested(String reference, String attribute, String value) {
        final String finalText = value;
        return (root, query, cb) -> {
            if (!StringUtils.hasText(finalText)) {
                return null;
            }
            Path<?> u = root.get(reference);
            return cb.equal(cb.lower(u.get(attribute)), value);
        };

    }

    public static Specification searchNestedLike(String reference, String attribute, String value) {
        final String finalText = value;
        return (root, query, cb) -> {
            if (!StringUtils.hasText(finalText)) {
                return null;
            }
            Path<?> u = root.get(reference);
            return cb.like(cb.lower(u.get(attribute)), contains(value.toLowerCase()));
        };
    }

    public static Specification searchNestedLike(String[] attributes, String value) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(value)) {
                return null;
            }
            Path<?> path = root.get(attributes[0]);
            int lastIndexAttributes = attributes.length - 1;
            for (int i = 1; i < lastIndexAttributes; i++) {
                path = path.get(attributes[i]);
            }
            return criteriaBuilder.like(path.get(attributes[lastIndexAttributes]), contains(value));
        };
    }

    public static Specification searchNestedLikeIgnoreCase(String[] attributes, String value) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(value)) {
                return null;
            }
            Path<?> path = root.get(attributes[0]);
            int lastIndexAttributes = attributes.length - 1;
            for (int i = 1; i < lastIndexAttributes; i++) {
                path = path.get(attributes[i]);
            }
            return criteriaBuilder.like(criteriaBuilder.lower(path.get(attributes[lastIndexAttributes])), contains(value.toLowerCase()));
        };
    }

    public static Specification searchNestedEqual(String reference, String attribute, String value) {
        final String finalText = value;
        return (root, query, cb) -> {
            if (!StringUtils.hasText(finalText)) {
                return null;
            }
            Path<?> u = root.get(reference);
            return cb.equal(u.get(attribute), value);
        };
    }

    public static Specification searchNestedEqual(String[] attributes, String value) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(value)) {
                return null;
            }
            Path<?> path = root.get(attributes[0]);
            int lastIndexAttributes = attributes.length - 1;
            for (int i = 1; i < lastIndexAttributes; i++) {
                path = path.get(attributes[i]);
            }
            return criteriaBuilder.equal(path.get(attributes[lastIndexAttributes]), value);
        };
    }

    public static Specification searchNestedEqualIgnoreCase(String[] attributes, String value) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(value)) {
                return null;
            }
            Path<?> path = root.get(attributes[0]);
            int lastIndexAttributes = attributes.length - 1;
            for (int i = 1; i < lastIndexAttributes; i++) {
                path = path.get(attributes[i]);
            }
            return criteriaBuilder.equal(
                    criteriaBuilder.lower(path.get(attributes[lastIndexAttributes])),
                    value.toLowerCase());
        };
    }

    public static Specification searchNestedEqual(String[] attributes, Object value) {
        return (root, query, criteriaBuilder) -> {
            if (null == value) {
                return null;
            }
            Path<?> path = root.get(attributes[0]);
            int lastIndexAttributes = attributes.length - 1;
            for (int i = 1; i < lastIndexAttributes; i++) {
                path = path.get(attributes[i]);
            }
            return criteriaBuilder.equal(path.get(attributes[lastIndexAttributes]), value);
        };
    }

    public static Specification searchNestedEqual(String reference, String attribute, Object value) {
        return (root, query, criteriaBuilder) -> {
            if (null == value) {
                return null;
            }
            Path<?> u = root.get(reference);
            return criteriaBuilder.equal(u.get(attribute), value);
        };
    }

    private static <T extends Comparable> Specification searchNestedGreaterThan(String[] attributes, T value) {
        return (root, query, criteriaBuilder) -> {
            if (null == value) {
                return null;
            }
            Path<?> path = root.get(attributes[0]);
            int lastIndexAttributes = attributes.length - 1;
            for (int i = 1; i < lastIndexAttributes; i++) {
                path = path.get(attributes[i]);
            }
            return criteriaBuilder.greaterThan(path.get(attributes[lastIndexAttributes]), value);
        };
    }

    public static <T extends Comparable> Specification searchNestedGreaterThan(String reference, String attribute, T value) {
        return (root, query, criteriaBuilder) -> {
            if (null == value) {
                return null;
            }
            Path<?> u = root.get(reference);
            return criteriaBuilder.greaterThan(u.get(attribute), value);
        };
    }

    private static <T extends Comparable> Specification searchNestedLessThan(String[] attributes, T value) {
        return (root, query, criteriaBuilder) -> {
            if (null == value) {
                return null;
            }
            Path<?> path = root.get(attributes[0]);
            int lastIndexAttributes = attributes.length - 1;
            for (int i = 1; i < lastIndexAttributes; i++) {
                path = path.get(attributes[i]);
            }
            return criteriaBuilder.lessThan(path.get(attributes[lastIndexAttributes]), value);
        };
    }

    public static <T extends Comparable> Specification searchNestedLessThan(String reference, String attribute, T value) {
        return (root, query, criteriaBuilder) -> {
            if (null == value) {
                return null;
            }
            Path<?> u = root.get(reference);
            return criteriaBuilder.lessThan(u.get(attribute), value);
        };
    }

    public static <T extends Comparable> Specification searchNestedGreaterThanOrEqualTo(String[] attributes, T value) {
        return (root, query, criteriaBuilder) -> {
            if (null == value) {
                return null;
            }
            Path<?> path = root.get(attributes[0]);
            int lastIndexAttributes = attributes.length - 1;
            for (int i = 1; i < lastIndexAttributes; i++) {
                path = path.get(attributes[i]);
            }
            return criteriaBuilder.greaterThanOrEqualTo(path.get(attributes[lastIndexAttributes]), value);
        };
    }

    public static <T extends Comparable> Specification searchNestedGreaterThanOrEqualTo(String reference, String attribute, T value) {
        return (root, query, criteriaBuilder) -> {
            if (null == value) {
                return null;
            }
            Path<?> u = root.get(reference);
            return criteriaBuilder.greaterThanOrEqualTo(u.get(attribute), value);
        };
    }

    public static <T extends Comparable> Specification searchNestedLessThanOrEqualTo(String[] attributes, T value) {
        return (root, query, criteriaBuilder) -> {
            if (null == value) {
                return null;
            }
            Path<?> path = root.get(attributes[0]);
            int lastIndexAttributes = attributes.length - 1;
            for (int i = 1; i < lastIndexAttributes; i++) {
                path = path.get(attributes[i]);
            }
            return criteriaBuilder.lessThanOrEqualTo(path.get(attributes[lastIndexAttributes]), value);
        };
    }

    public static <T extends Comparable> Specification searchNestedLessThanOrEqualTo(String reference, String attribute, T value) {
        return (root, query, criteriaBuilder) -> {
            if (null == value) {
                return null;
            }
            Path<?> u = root.get(reference);
            return criteriaBuilder.lessThanOrEqualTo(u.get(attribute), value);
        };
    }

    public static Specification searchLocalDateEqualTo(String attribute,
                                                       LocalDateTime time) {
        return (root, query, cb) -> {
            if (Objects.isNull(time)) {
                return null;
            }
            return cb.greaterThanOrEqualTo(root.get(attribute), time);
        };
    }

    public static Specification searchLocalDateGreaterThanEqualTo(String attribute,
                                                                  LocalDateTime time) {
        return (root, query, cb) -> {
            if (Objects.isNull(time)) {
                return null;
            }
            return cb.greaterThanOrEqualTo(root.get(attribute), time);
        };
    }


    public static Specification searchLocalDateLessThanEqualTo(String attribute,
                                                               LocalDateTime time) {
        return (root, query, cb) -> {
            if (Objects.isNull(time)) {
                return null;
            }
            return cb.lessThanOrEqualTo(root.get(attribute), time);
        };
    }

    public static Specification searchLocalDateLessThan(String attribute,
                                                        LocalDateTime time) {
        return (root, query, cb) -> {
            if (Objects.isNull(time)) {
                return null;
            }
            return cb.lessThan(root.get(attribute), time);
        };
    }

    public static Specification searchBetweenAttributeTimestamp(String attribute,
                                                                Timestamp from, Timestamp to) {
        return (root, query, cb) -> {
            if (Objects.isNull(from) || Objects.isNull(to)) {
                return null;
            }
            return cb.between(root.get(attribute), from, to);
        };
    }

    public static Specification searchBigDecimalEqualTo(String attribute, BigDecimal value) {
        return (root, query, cb) -> {
            if (Objects.isNull(value))
                return null;
            return cb.equal(root.get(attribute), value);
        };
    }

    public static <T> Specification<T> bySearch(Object dataSearch) {
        Specification<T> clause = (root, query, cb) -> null;
        var fields = dataSearch.getClass().getDeclaredFields();
        for (var field : fields) {
            if (!ObjectUtil.hasAnnotation(field, SearchParameter.class))
                continue;

            var value = ObjectUtil.getValueFromObject(dataSearch, field);
            if (value instanceof String stringValue) {
                clause = stringConstructSpecification(clause, getAttributeName(field), stringValue, getOperator(field));
            } else if (value instanceof Comparable comparableValue) {
                clause = comparableConstructSpecification(clause, getAttributeName(field), comparableValue, getOperator(field));
            } else if (value instanceof List) {
                clause = clause.and(searchAttributeIn(getAttributeName(field), (List<String>) value));
            }
        }
        return clause;
    }

    private static String getAttributeName(Field field) {
        SearchParameter annotation = field.getAnnotation(SearchParameter.class);
        if (StringUtils.hasText(annotation.attributeName())) {
            return annotation.attributeName();
        }
        return field.getName();
    }

    private static boolean isNestedAttribute(String attributeName) {
        return splitAttribute(attributeName, DELIMITER_ATTRIBUTE).length > 1;
    }

    private static String[] splitAttribute(String attributeRaw, String delimiter) {
        return attributeRaw.split(delimiter);
    }

    private static SearchOperator getOperator(Field field) {
        SearchParameter parameter = field.getAnnotation(SearchParameter.class);
        return parameter.operator();
    }

    private static <T> Specification<T> stringConstructSpecification(Specification<T> specification, String attributeName, String value, SearchOperator operator) {
        if (isNestedAttribute(attributeName)) {
            String[] attributes = splitAttribute(attributeName, DELIMITER_ATTRIBUTE);
            switch (operator) {
                case EQUAL_TO -> {
                    return specification.and(searchNestedEqual(attributes, value));
                }
                case EQUAL_IGNORE_CASE -> {
                    return specification.and(searchNestedEqualIgnoreCase(attributes, value));
                }
                case LIKE -> {
                    return specification.and(searchNestedLike(attributes, value));
                }
                case LIKE_IGNORE_CASE -> {
                    return specification.and(searchNestedLikeIgnoreCase(attributes, value));
                }
                default -> {
                    return specification;
                }
            }
        } else {
            switch (operator) {
                case EQUAL_TO -> {
                    return specification.and(searchAttributeEqual(attributeName, value));
                }
                case EQUAL_IGNORE_CASE -> {
                    return specification.and(searchAttributeEqualIgnoreCase(attributeName, value));
                }
                case LIKE -> {
                    return specification.and(searchAttributeLike(attributeName, value));
                }
                case LIKE_IGNORE_CASE -> {
                    return specification.and(searchAttributeLikeIgnoreCase(attributeName, value));
                }
                default -> {
                    return specification;
                }
            }
        }
    }

    private static <T, Y extends Comparable> Specification comparableConstructSpecification(Specification<T> specification, String attributeName, Y value, SearchOperator operator) {
        if (isNestedAttribute(attributeName)) {
            String[] attributes = splitAttribute(attributeName, DELIMITER_ATTRIBUTE);
            switch (operator) {
                case EQUAL_TO -> {
                    return specification.and(searchNestedEqual(attributes, value));
                }
                case GREATER_THAN -> {
                    return specification.and(searchNestedGreaterThan(attributes, value));
                }
                case LESS_THAN -> {
                    return specification.and(searchNestedLessThan(attributes, value));
                }
                case GREATER_THAN_EQUAL_TO -> {
                    return specification.and(searchNestedGreaterThanOrEqualTo(attributes, value));
                }
                case LESS_THAN_EQUAL_TO -> {
                    return specification.and(searchNestedLessThanOrEqualTo(attributes, value));
                }
                default -> {
                    return specification;
                }
            }
        } else {
            switch (operator) {
                case EQUAL_TO -> {
                    return specification.and(searchAttributeEqual(attributeName, value));
                }
                case GREATER_THAN -> {
                    return specification.and(searchAttributeGreaterThan(attributeName, value));
                }
                case LESS_THAN -> {
                    return specification.and(searchAttributeLessThan(attributeName, value));
                }
                case GREATER_THAN_EQUAL_TO -> {
                    return specification.and(searchAttributeGreaterThanOrEqualTo(attributeName, value));
                }
                case LESS_THAN_EQUAL_TO -> {
                    return specification.and(searchAttributeLessThanOrEqualTo(attributeName, value));
                }
                default -> {
                    return specification;
                }
            }
        }
    }

    public static Specification searchBetweenAttributeBigDecimal(String attribute,
                                                                 BigDecimal from, BigDecimal to) {
        return (root, query, cb) -> {
            if (null == from || null == to) {
                return null;
            }
            return cb.between(root.get(attribute), from, to);
        };
    }


    public static Specification searchListStringIn(String attribute, List<String> list) {
        return (root, query, cb) -> {
            if (null == list || list.isEmpty()) {
                return null;
            }
            return root.get(attribute).in(list);
        };
    }

    public static Specification searchNestedListStringIn(String attribute, List<String> ids) {
        String[] attributes = splitAttribute(attribute, DELIMITER_ATTRIBUTE);
        return (root, query, criteriaBuilder) -> {
            if (null == ids || ids.isEmpty())
                return null;
            Path<?> path = root.get(attributes[0]);
            int lastIndexAttributes = attributes.length - 1;
            for (int i = 1; i < lastIndexAttributes; i++) {
                path = path.get(attributes[i]);
            }
            return path.get(attributes[lastIndexAttributes]).in(ids);
        };
    }

    public static Specification searchListStringNotIn(String attribute, List<String> list) {
        return (root, query, cb) -> {
            if (null == list || list.isEmpty()) {
                return null;
            }
            return root.get(attribute).in(list).not();
        };
    }

    public static <T> Specification<T> combineSpecifications(Specification<T>... specifications) {
        Specification<T> result = Specification.where(null);
        for (Specification<T> spec : specifications) {
            if (spec != null) {
                result = result.and(spec);
            }
        }
        return result;
    }

}
