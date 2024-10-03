package com.data.utils.page.domain;

import java.util.List;
import java.util.function.Function;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class DataPageImpl<T> extends DataChunk<T> implements Page<T> {

  public DataPageImpl(List<T> content, Pageable pageable) {
    super(content, pageable);
  }

  @Override
  public int getTotalPages() {
    return 0;
  }

  @Override
  public long getTotalElements() {
    return 0;
  }

  @Override
  public boolean hasNext() {
    return false;
  }

  @Override
  public <U> Page<U> map(Function<? super T, ? extends U> converter) {
    return null;
  }
}
