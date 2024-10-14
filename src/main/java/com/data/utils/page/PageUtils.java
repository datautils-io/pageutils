package com.data.utils.page;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.util.CollectionUtils;

/**
 * Utility class for handling pagination and sorting operations with {@link Page} and {@link Slice}
 * objects. This class provides methods to convert lists of objects into paginated and sorted {@link
 * Page} and {@link Slice} objects, as well as to update {@link Pageable} objects based on provided
 * sort mappings.
 */
public class PageUtils {

  private PageUtils() {}

  /**
   * Calculates the limit for pagination based on the given Pageable object.
   *
   * @param pageable the Pageable object containing pagination information
   * @return the limit for the current page
   */
  public static int getLimit(Pageable pageable) {
    int pageNumber = pageable.getPageNumber();
    int pageSize = pageable.getPageSize();

    return pageNumber == 0 ? pageSize : (pageNumber + 1) * pageSize;
  }

  /**
   * Converts the given list of objects to a {@link Page} object, using the specified {@link
   * Pageable} object for pagination and sorting.
   *
   * @param list the list of objects to include in the page
   * @param pageable the {@link Pageable} object containing the pagination and sort criteria
   * @param <T> the type of the objects in the list
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

    if (start >= list.size()) return Page.empty();

    List<T> sortedList = SortingUtils.sort(list, pageable);

    return new PageImpl<>(sortedList.subList(start, end), pageable, sortedList.size());
  }

  /**
   * Converts the given list of objects to a {@link Slice} object, using the specified {@link
   * Pageable} object for pagination and sorting.
   *
   * @param list the list of objects to include in the slice
   * @param pageable the {@link Pageable} object containing the pagination and sort criteria
   * @param <T> the type of the objects in the list
   * @return a {@link Slice} object with the specified page size, page number, and sorting criteria
   */
  public static <T> Slice<T> toSlice(List<T> list, Pageable pageable) {

    if (CollectionUtils.isEmpty(list)) {
      return new SliceImpl<>(List.of(), pageable, false);
    }

    if (pageable == null) {
      pageable = Pageable.ofSize(list.size());
    }

    int start = Math.toIntExact(pageable.getOffset());
    int end = Math.min((start + pageable.getPageSize()), list.size());

    if (start >= list.size()) return Page.empty();

    List<T> sortedList = SortingUtils.sort(list, pageable);

    boolean hasNext = end < sortedList.size();

    return new SliceImpl<>(sortedList.subList(start, end), pageable, hasNext);
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
}
