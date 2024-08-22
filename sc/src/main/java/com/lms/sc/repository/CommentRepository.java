package com.lms.sc.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lms.sc.entity.Comment;
import com.lms.sc.entity.Board;
import com.lms.sc.entity.SiteUser;


public interface CommentRepository extends JpaRepository<Comment, Integer>{
	List<Comment> findAllByBoard(Board board);
	
	void deleteAllByAuthor(SiteUser user);
}
