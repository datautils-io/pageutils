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

public abstract class DataChunk<T> implements Slice<T>, Serializable {

  @Serial private static final long serialVersionUID = 5262034506603260731L;

  private final List<T> content = new ArrayList<>();
  private final Pageable pageable;

  protected DataChunk(List<T> content, Pageable pageable) {
    this.content.addAll(content);
    this.pageable = pageable;
  }

  @Override
  public int getNumber() {
    return pageable.isPaged() ? pageable.getPageNumber() : 0;
  }

  @Override
  public int getSize() {
    return pageable.isPaged() ? pageable.getPageSize() : content.size();
  }

  @Override
  public int getNumberOfElements() {
    return content.size();
  }

  @Override
  public List<T> getContent() {
    return content;
  }

  @Override
  public boolean hasContent() {
    return !content.isEmpty();
  }

  @Override
  public Sort getSort() {
    return pageable.getSort();
  }

  @Override
  public boolean isFirst() {
    return getNumber() == 0;
  }

  @Override
  public boolean isLast() {
    return !hasNext();
  }

  @Override
  public boolean hasNext() {
    return getNumberOfElements() == getSize();
  }

  @Override
  public boolean hasPrevious() {
    return getNumber() > 0;
  }

  @Override
  public Pageable nextPageable() {
    return hasNext() ? pageable.next() : Pageable.unpaged();
  }

  @Override
  public Pageable previousPageable() {
    return hasPrevious() ? pageable.previousOrFirst() : Pageable.unpaged();
  }

  @Override
  public Iterator<T> iterator() {
    return content.iterator();
  }

  public <U> List<U> getConvertedContent(Function<? super T, ? extends U> converter) {
    return this.stream().map(converter).collect(Collectors.toList());
  }
}
