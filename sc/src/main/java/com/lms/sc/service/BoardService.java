package com.lms.sc.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.sc.entity.Board;
import com.lms.sc.entity.Lecture;
import com.lms.sc.entity.SiteUser;
import com.lms.sc.exception.DataNotFoundException;
import com.lms.sc.repository.BoardRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class BoardService {
	private final BoardRepository boardRepository;
	
	@Transactional(readOnly = true)
	public Page<Board> getList(int page){
		List<Sort.Order> sorts = new ArrayList<>();
		sorts.add(Sort.Order.desc("createDate"));
		Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
		Page<Board> boards = boardRepository.findAll(pageable);
		boards.forEach(board -> Hibernate.initialize(board.getCommentList()));
        return boards;
	}
	
	public List<Board> getList() { 
		return this.boardRepository.findAll(); 
	}
	
	public Page<Board> getListByAuthor(SiteUser author, int page) {
		List<Sort.Order> sorts = new ArrayList<>();
		sorts.add(Sort.Order.desc("createDate"));
		Pageable pageable = PageRequest.of(page, 10);
		return this.boardRepository.findByAuthor(author, pageable);
	}

	public List<Board> getRecentBoards(SiteUser author) {
		Pageable pageable = PageRequest.of(0, 3);
		return boardRepository.findTop3ByAuthor(author, pageable);
	}

	public Board getBoard(Integer id) {
		Optional<Board> board = this.boardRepository.findById(id);
		if(board.isPresent()) {
			return board.get();
		}else {
			throw new DataNotFoundException("board not found");
		}
	}
	
	// public Board updateResolve(Board board) {
	// 	boolean resolved = true;
	// 	board.setResult(resolved);
	// 	return boardRepository.save(board);
	// }
	
	public void create(String title, String content, SiteUser author) {
		Board q = new Board();
		q.setTitle(title);
		q.setContent(content);
		q.setAuthor(author);
		q.setCreateDate(LocalDateTime.now());
		this.boardRepository.save(q);
	}
	
	public void delete(Board board) {
		this.boardRepository.delete(board);
	}
	
	public void modify(Board board, String title, String content) {
		board.setTitle(title);
		board.setContent(content);
		board.setModifyDate(LocalDateTime.now());
		this.boardRepository.save(board);
	}
	
	//비디오뷰에서 질문을 저장
	public Board createboard(String title, String content, SiteUser author) {
        Board board = new Board();
        board.setTitle(title);
        board.setContent(content);
        board.setAuthor(author);
        board.setCreateDate(LocalDateTime.now());
//        board.setLikeCnt(0);
//        board.setResult(false);
        return boardRepository.save(board);
    }
	
	public Page<Board> searchBoards(int page, String keyword) {
		Pageable pageable = PageRequest.of(page, 10, Sort.by("createDate").descending());
		return boardRepository.findByKeywordWithAnswers(keyword, pageable);
	}
	
//	public List<board> getboardByLecture(Lecture lecture){
//		return boardRepository.findByVideo_Lecture(lecture);
//	}
	
}
