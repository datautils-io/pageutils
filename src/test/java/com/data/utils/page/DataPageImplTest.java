package com.data.utils.page;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.data.utils.page.domain.DataPageImpl;

public class DataPageImplTest {

	@Test
	public void testDataPageImpl() {
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
	public void testSort() {
		List<String> content = List.of("banana", "apple", "cherry");
		Pageable pageable = PageRequest.of(0, 3);
		long total = 3;
		String sortSeparator = "_";

		DataPageImpl<String> dataPage = new DataPageImpl<>(content, pageable, total, sortSeparator);

		// Sort the content
		dataPage.sort();

		// Verify the sorted content
		List<String> sortedContent = dataPage.getContent();
		assertEquals(List.of("apple", "banana", "cherry"), sortedContent);
	}

}
