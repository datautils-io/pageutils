# Page Utils Library

## Overview
The Page Utils Library is a Java utility library designed to facilitate pagination and sorting operations for collections of objects. It provides methods to convert lists into `Page` and `Slice` objects, as well as utilities for sorting based on specified criteria.

## Features
- Convert lists to `Page` and `Slice` objects for easy pagination.
- Sort lists based on multiple fields using custom sorting logic.
- Support for nested properties in sorting.

## Dependencies
This library requires the following dependencies:
- Spring Data Commons

Make sure to include the following in your `pom.xml` if you are using Maven:

`xml`

    <dependency>
      <groupId>org.springframework.data</groupId>
      <artifactId>spring-data-commons</artifactId>
      <version>3.3.4</version>
    </dependency>

## Usage

### 1. Convert List to Page
To convert a list of objects to a `Page` object:

`java`

    import org.springframework.data.domain.Page;
    import org.springframework.data.domain.PageRequest;
    import org.springframework.data.domain.Pageable;

    List<MyObject> myList = ...; // Your list of objects
    Pageable pageable = PageRequest.of(0, 10); // Page number and size
    Page<MyObject> page = PageUtils.toPage(myList, pageable);`

### 2. Convert List to Slice
To convert a list of objects to a `Slice` object:

`java`

    import org.springframework.data.domain.Slice;
    import org.springframework.data.domain.PageRequest;
    import org.springframework.data.domain.Pageable;

    List<MyObject> myList = ...; // Your list of objects
    Pageable pageable = PageRequest.of(0, 10); // Page number and size
    Slice<MyObject> slice = PageUtils.toSlice(myList, pageable);`

### 3. Sorting
To sort a list of objects:

`java`

    import org.springframework.data.domain.Sort;
    List<MyObject> myList = ...; // Your list of objects
    Sort sort = Sort.by("fieldName"); // Specify the field to sort by
    List<MyObject> sortedList = SortingUtils.sortList(myList, sort);

### 4. Update Pageable
To update a `Pageable` object with new sort field mappings:

`java`
    
    import org.springframework.data.domain.Pageable;
    import java.util.HashMap;
    import java.util.Map;

    Pageable pageable = ...; // Your existing Pageable
    Map<String, String> sortMap = new HashMap<>();
    sortMap.put("oldFieldName", "newFieldName");
    Pageable updatedPageable = PageUtils.updatePageable(pageable, sortMap, true);
