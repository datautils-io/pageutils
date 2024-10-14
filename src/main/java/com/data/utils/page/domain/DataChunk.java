package com.data.utils.page.domain;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

/**
 * Abstract class representing a chunk of data with pagination and sorting capabilities. This class
 * provides a common interface for handling lists of data in a paginated manner.
 *
 * @param <T> the type of elements in this data chunk
 */
public abstract class DataChunk<T> implements Slice<T>, Serializable {

  @Serial private static final long serialVersionUID = 5262034506603260731L;

  private final List<T> content = new ArrayList<>();
  private final Pageable pageable;

  /**
   * Constructs a new DataChunk with the specified content and pageable information.
   *
   * @param content the content of the data chunk
   * @param pageable the pageable information for the data chunk
   */
  protected DataChunk(List<T> content, Pageable pageable) {
    this.content.addAll(content);
    this.pageable = pageable;
  }

  /**
   * Returns the number of the page represented by this data chunk.
   *
   * @return the number of the page
   */
  @Override
  public int getNumber() {
    return pageable.isPaged() ? pageable.getPageNumber() : 0;
  }

  /**
   * Returns the size of this data chunk.
   *
   * @return the size of this data chunk
   */
  @Override
  public int getSize() {
    return pageable.isPaged() ? pageable.getPageSize() : content.size();
  }

  /**
   * Returns the number of elements in this data chunk.
   *
   * @return the number of elements in this data chunk
   */
  @Override
  public int getNumberOfElements() {
    return content.size();
  }

  /**
   * Returns the content of this data chunk.
   *
   * @return the content of this data chunk
   */
  @Override
  public List<T> getContent() {
    return content;
  }

  /**
   * Checks if this data chunk has content.
   *
   * @return true if this data chunk has content, false otherwise
   */
  @Override
  public boolean hasContent() {
    return !content.isEmpty();
  }

  /**
   * Returns the sort information for this data chunk.
   *
   * @return the sort information for this data chunk
   */
  @Override
  public Sort getSort() {
    return pageable.getSort();
  }

  /**
   * Checks if this is the first page.
   *
   * @return true if this is the first page, false otherwise
   */
  @Override
  public boolean isFirst() {
    return getNumber() == 0;
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
   * Checks if there is a next page available.
   *
   * @return true if there is a next page, false otherwise
   */
  @Override
  public boolean hasNext() {
    return getNumberOfElements() == getSize();
  }

  /**
   * Checks if there is a previous page available.
   *
   * @return true if there is a previous page, false otherwise
   */
  @Override
  public boolean hasPrevious() {
    return getNumber() > 0;
  }

  /**
   * Returns the next pageable information for this data chunk.
   *
   * @return the next pageable information for this data chunk
   */
  @Override
  public Pageable nextPageable() {
    return hasNext() ? pageable.next() : Pageable.unpaged();
  }

  /**
   * Returns the previous pageable information for this data chunk.
   *
   * @return the previous pageable information for this data chunk
   */
  @Override
  public Pageable previousPageable() {
    return hasPrevious() ? pageable.previousOrFirst() : Pageable.unpaged();
  }

  /**
   * Returns an iterator over the elements in this data chunk.
   *
   * @return an iterator over the elements in this data chunk
   */
  @Override
  public Iterator<T> iterator() {
    return content.iterator();
  }

  /**
   * Converts the content of this data chunk to a new type using the provided converter function.
   *
   * @param converter the function to convert elements of type T to type U
   * @param <U> the type of elements in the new data chunk
   * @return a new DataChunkImpl containing the converted content
   */
  public <U> List<U> getConvertedContent(Function<? super T, ? extends U> converter) {
    return this.stream().map(converter).collect(Collectors.toList());
  }

  /**
   * Updates the content of this data chunk with new content.
   *
   * @param newContent the new content to set in this data chunk
   */
  public void updateContent(List<T> newContent) {
    content.clear();
    content.addAll(newContent);
  }
}
