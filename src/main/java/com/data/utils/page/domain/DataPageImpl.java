package com.data.utils.page.domain;

import com.data.utils.page.SortingUtils;
import java.util.List;
import java.util.function.Function;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Implementation of the {@link Page} interface that represents a paginated list of data. This class
 * provides additional functionality for handling pagination, sorting, and mapping of content within
 * a page.
 *
 * @param <T> the type of elements in this page
 */
public class DataPageImpl<T> extends DataChunk<T> implements Page<T> {

  /** The total number of elements available in the entire dataset. */
  private final long total;

  /** The separator used for sorting nested properties. */
  private final String sortSeparator;

  /**
   * Constructs a new DataPageImpl with the specified content, pageable information, and total
   * count.
   *
   * @param content the content of the page
   * @param pageable the pageable information for the page
   * @param total the total number of elements available
   */
  public DataPageImpl(List<T> content, Pageable pageable, long total) {
    super(content, pageable);

    this.total =
        pageable
            .toOptional()
            .filter(it -> !content.isEmpty())
            .filter(it -> it.getOffset() + it.getPageSize() > total)
            .map(it -> it.getOffset() + content.size())
            .orElse(total);

    this.sortSeparator = "_";
  }

  /**
   * Constructs a new DataPageImpl with the specified content and default pageable information.
   *
   * @param content the content of the page
   */
  public DataPageImpl(List<T> content) {
    this(content, Pageable.unpaged(), null == content ? 0 : content.size());
  }

  /**
   * Constructs a new DataPageImpl with the specified content, pageable information, total count,
   * and sort separator.
   *
   * @param content the content of the page
   * @param pageable the pageable information for the page
   * @param total the total number of elements available
   * @param sortSeparator the separator used for sorting nested properties
   */
  public DataPageImpl(List<T> content, Pageable pageable, long total, String sortSeparator) {
    super(content, pageable);

    this.total =
        pageable
            .toOptional()
            .filter(it -> !content.isEmpty())
            .filter(it -> it.getOffset() + it.getPageSize() > total)
            .map(it -> it.getOffset() + content.size())
            .orElse(total);

    this.sortSeparator = sortSeparator;
  }

  /**
   * Returns the total number of pages available based on the total number of elements and the size
   * of the page.
   *
   * @return the total number of pages
   */
  @Override
  public int getTotalPages() {
    return getSize() == 0 ? 1 : (int) Math.ceil((double) total / (double) getSize());
  }

  /**
   * Returns the total number of elements available in the entire dataset.
   *
   * @return the total number of elements
   */
  @Override
  public long getTotalElements() {
    return total;
  }

  /**
   * Checks if there is a next page available.
   *
   * @return true if there is a next page, false otherwise
   */
  @Override
  public boolean hasNext() {
    return getNumber() + 1 < getTotalPages();
  }

  /**
   * Checks if this is the last page.
   *
   * @return true if this is the last page, false otherwise
   */
  @Override
  public boolean isLast() {
    return !hasNext();
  }

  /**
   * Maps the content of this page to a new page of a different type using the provided converter
   * function.
   *
   * @param converter the function to convert elements of type T to type U
   * @param <U> the type of elements in the new page
   * @return a new page containing the converted content
   */
  @Override
  public <U> Page<U> map(Function<? super T, ? extends U> converter) {
    return new DataPageImpl<>(getConvertedContent(converter), getPageable(), total);
  }

  /** Sorts the content of this page using the specified sorting utility. */
  public void sort() {
    super.updateContent(SortingUtils.sort(getContent(), getPageable(), sortSeparator));
  }
}
