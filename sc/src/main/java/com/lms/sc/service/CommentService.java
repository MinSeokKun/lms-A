package com.lms.sc.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.lms.sc.entity.Comment;
import com.lms.sc.entity.Board;
import com.lms.sc.entity.SiteUser;
import com.lms.sc.exception.DataNotFoundException;
import com.lms.sc.repository.CommentRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CommentService {
	
	private final CommentRepository commentRepository;
	
//	등록
	public void create(Board board, String content, SiteUser author) {
		Comment answer = new Comment();
		answer.setContent(content);
		answer.setAuthor(author);
		answer.setCreateDate(LocalDateTime.now());
		answer.setBoard(board);
		this.commentRepository.save(answer);
	}
	
	public List<Comment> getAnswerList(Board board){
		return commentRepository.findAllByBoard(board);
	}
	

	public Comment getAnswer(Integer id) {
		Optional<Comment> answer= this.commentRepository.findById(id);
		if(answer.isPresent()) {
			return answer.get();
		}else {
			throw new DataNotFoundException("answer not found");
		}
	}
	
	public void modify(Comment answer, String content) {
		answer.setContent(content);
		answer.setModifyDate(LocalDateTime.now());
		this.commentRepository.save(answer);
	}
	
	public void delete(Comment answer) {
		this.commentRepository.delete(answer);
	}
	
	
}
