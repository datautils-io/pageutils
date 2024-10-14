package com.data.utils.page;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.data.utils.page.domain.DataPageImpl;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

class DataPageImplTest {

  @Test
  void testDataPageImpl() {
    List<String> content = List.of("item1", "item2", "item3");
    Pageable pageable = PageRequest.of(0, 2);
    long total = 3;

    DataPageImpl<String> dataPage = new DataPageImpl<>(content, pageable, total);

    assertEquals(2, dataPage.getSize());
    assertEquals(3, dataPage.getTotalElements());
    assertTrue(dataPage.hasNext());
    assertFalse(dataPage.isLast());
  }

  @Test
  void testSort() {
    List<String> content = List.of("banana", "apple", "cherry");
    Pageable pageable = PageRequest.of(0, 3);
    long total = 3;
    String sortSeparator = "_";

    DataPageImpl<String> dataPage = new DataPageImpl<>(content, pageable, total, sortSeparator);

    dataPage.sort();

    List<String> sortedContent = dataPage.getContent();
    assertEquals(List.of("apple", "banana", "cherry"), sortedContent);
  }
}
