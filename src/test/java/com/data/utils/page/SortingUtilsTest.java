package com.data.utils.page;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class SortingUtilsTest {

	@Test
	public void testSortListAscending() {
		List<TestObject> list = List.of(
				new TestObject("B", 2),
				new TestObject("A", 1),
				new TestObject("C", 3)
		);
		Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "name"));

		List<TestObject> sortedList = SortingUtils.sort(list, pageable);

		assertEquals("A", sortedList.get(0).getName());
		assertEquals("B", sortedList.get(1).getName());
		assertEquals("C", sortedList.get(2).getName());
	}

	@Test
	public void testSortListDescending() {
		List<TestObject> list = List.of(
				new TestObject("B", 2),
				new TestObject("A", 1),
				new TestObject("C", 3)
		);
		Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "name"));

		List<TestObject> sortedList = SortingUtils.sort(list, pageable);

		assertEquals("C", sortedList.get(0).getName());
		assertEquals("B", sortedList.get(1).getName());
		assertEquals("A", sortedList.get(2).getName());
	}

	@Test
	public void testSortListWithNestedObjects() {
		List<ParentObject> list = List.of(
				new ParentObject(new ChildObject("B", 2)),
				new ParentObject(new ChildObject("A", 1)),
				new ParentObject(new ChildObject("C", 3))
		);
		Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "child_name"));

		List<ParentObject> sortedList = SortingUtils.sort(list, pageable);

		assertEquals("A", sortedList.get(0).getChild().getName());
		assertEquals("B", sortedList.get(1).getChild().getName());
		assertEquals("C", sortedList.get(2).getChild().getName());
	}

	private static class TestObject {
		private String name;
		private int value;

		public TestObject(String name, int value) {
			this.name = name;
			this.value = value;
		}

		public String getName() {
			return name;
		}

		public int getValue() {
			return value;
		}
	}

	private static class ParentObject {
		private ChildObject child;

		public ParentObject(ChildObject child) {
			this.child = child;
		}

		public ChildObject getChild() {
			return child;
		}
	}

	private static class ChildObject {
		private final String name;
		private final int value;

		public ChildObject(String name, int value) {
			this.name = name;
			this.value = value;
		}

		public String getName() {
			return name;
		}

		public int getValue() {
			return value;
		}
	}
}
