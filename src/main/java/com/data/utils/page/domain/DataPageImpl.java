package com.data.utils.page.domain;

import java.util.function.Function;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class DataPageImpl<T> extends DataChunk<T> implements Page<T> {

  @Override
  public boolean hasNext() {
    return false;
  }

  @Override
  public Pageable nextOrLastPageable() {
    return super.nextOrLastPageable();
  }

  @Override
  public Pageable previousOrFirstPageable() {
    return super.previousOrFirstPageable();
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
  public <U> Page<U> map(Function<? super T, ? extends U> converter) {
    return null;
  }
}
