package com.data.utils.page;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/** Utility class for sorting operations. */
public class SortingUtils {

    private SortingUtils() {}

    /**
     * Sorts the given list of objects using the specified sort orders.
     *
     * @param list the list of objects to sort
     * @param orders the sort orders to apply
     * @param <T> the type of the objects in the list
     * @return a sorted list of objects
     */
    public static <T> List<T> sortList(List<T> list, Pageable pageable) {
        List<T> copy = new ArrayList<>(list);
        List<Sort.Order> orders = pageable.getSort().get().toList();
        Comparator<T> comparator = createComparator(orders);
        copy.sort(comparator);
        return copy;
    }

    /**
     * Creates a Comparator for sorting a list of objects using the given sort orders.
     *
     * @param orders the sort orders to use for sorting
     * @param <T> the type of the objects in the list
     * @return A Comparator that can be used to sort the list of objects
     */
    private static <T> Comparator<T> createComparator(List<Sort.Order> orders) {
        return (o1, o2) -> {
            for (Sort.Order order : orders) {
                String sortField = order.getProperty();
                Sort.Direction direction = order.getDirection();

                Object value1 = getFlattenedValue(o1, sortField);
                Object value2 = getFlattenedValue(o2, sortField);

                int comparisonResult = compareValues(value1, value2, direction);
                if (comparisonResult != 0) {
                    return comparisonResult;
                }
            }
            return 0;
        };
    }

    @SuppressWarnings("unchecked")
    private static int compareValues(Object value1, Object value2, Sort.Direction direction) {
        if (value1 == null && value2 == null) {
            return 0;
        } else if (value1 == null) {
            return (direction == Sort.Direction.ASC) ? -1 : 1;
        } else if (value2 == null) {
            return (direction == Sort.Direction.ASC) ? 1 : -1;
        }
        if (value1 instanceof Comparable<?> && value2 instanceof Comparable<?>) {
            int result = ((Comparable<Object>) value1).compareTo(value2);
            return (direction == Sort.Direction.ASC) ? result : -result;
        }
        return 0; // Fallback if not comparable
    }

    private static Object getFlattenedValue(Object obj, String sortField) {
        String[] keys = sortField.split("_");
        Object currentObj = obj;

        for (String key : keys) {
            if (currentObj == null) {
                break;
            }

            if (currentObj instanceof Map<?, ?>) {
                currentObj = ((Map<?, ?>) currentObj).get(key);
            } else {
                currentObj = getFieldOrNestedField(currentObj, key);
            }
        }

        return currentObj;
    }

    private static Object getFieldOrNestedField(Object obj, String fieldName) {
        Field field = getField(obj.getClass(), fieldName);
        if (field != null) {
            field.setAccessible(true);
            try {
                return field.get(obj);
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException("Error accessing field: " + fieldName, e);
            }
        }
        return null;
    }

    private static Field getField(Class<?> clazz, String fieldName) {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            if (clazz.getSuperclass() != null) {
                return getField(clazz.getSuperclass(), fieldName);
            }
            return null;
        }
    }
}
