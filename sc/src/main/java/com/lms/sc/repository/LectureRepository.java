package com.lms.sc.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lms.sc.entity.Lecture;
import com.lms.sc.entity.SiteUser;
import java.util.List;


public interface LectureRepository extends JpaRepository<Lecture, Long> {
	Optional<Lecture> findById(long id);
	
	List<Lecture> findByAuthor(SiteUser author);
//	@Query("SELECT l FROM Lecture l LEFT JOIN FETCH l.students WHERE l.id = :lecId")
//	Optional<Lecture> findByIdWithStudents(@Param("lecId") long lecId);
	
}
