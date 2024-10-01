package com.data.utils.page;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.util.CollectionUtils;

/** Utility class for {@link Page} object. */
public class PageUtils {

  private PageUtils() {}

  public static int getLimit(Pageable pageable) {
    int pageNumber = pageable.getPageNumber();
    int pageSize = pageable.getPageSize();

    return pageNumber == 0 ? pageSize : (pageNumber + 1) * pageSize;
  }

  /**
   * Converts the given list of objects to a {@link Page} object, using the given {@link Pageable}
   * object for pagination and sorting.
   *
   * @param list the list of objects to include in the page
   * @param pageable the {@link Pageable} object containing the pagination and sort criteria
   * @return a {@link Page} object with the specified page size, page number, and sorting criteria
   */
  public static <T> Page<T> toPage(List<T> list, Pageable pageable) {

    if (CollectionUtils.isEmpty(list)) {
      return Page.empty();
    }

    if (pageable == null) {
      pageable = PageRequest.of(0, list.size());
    }

    int start = Math.toIntExact(pageable.getOffset());

    int end = Math.min((start + pageable.getPageSize()), list.size());

    if (start >= list.size()) {
      return Page.empty();
    }

    List<T> sortedList = sortList(list, pageable);

    return new PageImpl<>(sortedList.subList(start, end), pageable, sortedList.size());
  }

  /**
   * Sorts the given list of objects using the sort criteria from the given {@link Pageable} object.
   *
   * @param list the list of objects to sort
   * @param pageable the {@link Pageable} object containing the sort criteria
   */
  public static <T> List<T> sortList(List<T> list, Pageable pageable) {

    List<T> copy = new ArrayList<>(list);

    List<Sort.Order> orders = pageable.getSort().get().toList();

    Comparator<T> comparator = createComparator(orders);

    copy.sort(comparator);

    return copy;
  }

  /**
   * Updates the Pageable object by mapping the sort fields to new names according to the provided
   * mapping.
   *
   * @param pageable the Pageable object to be updated
   * @param sortMap the map of old sort field names to new sort field names
   * @param keepAll if false, discard columns from the original sort list that are not mapped in the
   *     sort map; if true, retain all columns from the original sort list and update the columns
   *     specified in the sort map
   * @return the updated Pageable object
   */
  public static Pageable updatePageable(
      Pageable pageable, Map<String, String> sortMap, boolean keepAll) {
    List<Sort.Order> orders = new ArrayList<>();
    for (Order order : pageable.getSort()) {
      String mappedSortField = sortMap.get(order.getProperty());
      if (mappedSortField != null) {
        orders.add(new Order(order.getDirection(), mappedSortField));
        if (!keepAll) {
          return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(orders));
        }
      } else if (keepAll) {
        orders.add(order);
      }
    }
    return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(orders));
  }

  /**
   * Creates a Comparator for sorting a list of objects using the given sort orders and class.
   *
   * @param orders the sort orders to use for sorting
   * @param <T> the type of the objects in the list
   * @return A Comparator that can be used to sort the list of objects
   */
  @SuppressWarnings("unchecked")
  public static <T> Comparator<T> createComparator(List<Sort.Order> orders) {
    return (o1, o2) -> {
      for (Sort.Order order : orders) {
        String sortField = order.getProperty();
        Sort.Direction direction = order.getDirection();

        Object value1 = getFlattenedValue(o1, sortField);
        Object value2 = getFlattenedValue(o2, sortField);

        if (value1 == null && value2 == null) {
          continue;
        } else if (value1 == null) {
          return (direction == Direction.ASC) ? -1 : 1;
        } else if (value2 == null) {
          return (direction == Direction.ASC) ? 1 : -1;
        }
        if (value1 instanceof String stringValue1 && value2 instanceof String stringValue2) {
          int result = stringValue1.compareToIgnoreCase(stringValue2);
          return (direction == Direction.ASC) ? result : -result;
        }
        if (value1 instanceof Comparable<?> && value2 instanceof Comparable<?>) {
          int result = ((Comparable<Object>) value1).compareTo(value2);
          return (direction == Direction.ASC) ? result : -result;
        }
      }
      return 0;
    };
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
