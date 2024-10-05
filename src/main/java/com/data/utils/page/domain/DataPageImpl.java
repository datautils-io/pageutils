package com.data.utils.page.domain;

import com.data.utils.page.SortingUtils;
import java.util.List;
import java.util.function.Function;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public class DataPageImpl<T> extends DataChunk<T> implements Page<T> {

  private final long total;

  private final String sortSeparator;

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

  public DataPageImpl(List<T> content) {
    this(content, Pageable.unpaged(), null == content ? 0 : content.size());
  }

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

  @Override
  public int getTotalPages() {
    return getSize() == 0 ? 1 : (int) Math.ceil((double) total / (double) getSize());
  }

  @Override
  public long getTotalElements() {
    return total;
  }

  @Override
  public boolean hasNext() {
    return getNumber() + 1 < getTotalPages();
  }

  @Override
  public boolean isLast() {
    return !hasNext();
  }

  @Override
  public <U> Page<U> map(Function<? super T, ? extends U> converter) {
    return new PageImpl<>(getConvertedContent(converter), getPageable(), total);
  }

  public DataPageImpl<T> sort() {
    List<T> sortedContent = SortingUtils.sort(getContent(), super.getPageable(), sortSeparator);
    return new DataPageImpl<>(sortedContent, getPageable(), total);
  }
}
