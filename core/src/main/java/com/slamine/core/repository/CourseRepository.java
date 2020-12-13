package com.slamine.core.repository;

import com.slamine.core.model.Course;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 *
 */
@Repository
public interface CourseRepository extends PagingAndSortingRepository<Course, Long> {
}
