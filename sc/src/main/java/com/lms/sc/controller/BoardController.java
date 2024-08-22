package com.lms.sc.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import com.lms.sc.createForm.BoardCreateForm;
import com.lms.sc.createForm.CommentCreateForm;
import com.lms.sc.entity.Board;
import com.lms.sc.entity.Comment;
import com.lms.sc.entity.SiteUser;
import com.lms.sc.exception.DataNotFoundException;
import com.lms.sc.service.BoardService;
import com.lms.sc.service.CommentService;
import com.lms.sc.service.UserService;
import com.lms.sc.service.VideoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/board")
public class BoardController {

	private final BoardService boardService;
	private final CommentService commentService;
	private final UserService userService;
	private final VideoService videoService;
	
	@GetMapping("/list") 
	public String list(Model model, @RequestParam(value ="page", defaultValue = "0") int page) {
		Page<Board> paging = this.boardService.getList(page);
		model.addAttribute("paging", paging);
		return "board/board_list";
	}
	
	@GetMapping("/detail/{id}")
	public String detail(Model model, @PathVariable("id") Integer id, CommentCreateForm answerCreateForm) {
		Board board = this.boardService.getBoard(id);
		if (board == null) {
			throw new DataNotFoundException("Board not found");
		}
		List<Comment> commentList = commentService.getAnswerList(board);
		model.addAttribute("commentList", commentList);
		model.addAttribute("board", board);
		return "board/board_detail";
	}
	
	@GetMapping("/create")
	public String BoardCreate(BoardCreateForm BoardCreateForm) {
		return "board/board_form";
	}
	
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/create") 
	public String BoardCreate(@Valid BoardCreateForm BoardCreateForm, BindingResult bindingResult, Principal principal) {
		if (bindingResult.hasErrors()) {
			return "board/board_form";
		}
		this.boardService.create(BoardCreateForm.getTitle(), BoardCreateForm.getContent(), userService.getUserByEmail(principal.getName()));
		return "redirect:/board/list"; //질문 저장 후 질문 목록으로 이동 }
	}
	
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/delete/{id}")
	public String BoardDelete(Model model, @PathVariable("id") Integer id, Principal principal) {
		Board Board = this.boardService.getBoard(id);
		if (Board == null) {
			throw new DataNotFoundException("Board not found");
		}
		
		if(!Board.getAuthor().getEmail().equals(principal.getName())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제 권한이 없습니다.");
		}
		
		boardService.delete(Board);
		
		return "redirect:/board/list";
	}
	
	// 질문 해결
	// @PreAuthorize("isAuthenticated()")
	// @GetMapping("/resolve/{questId}")
	// public String BoardResolved(@PathVariable("questId") Integer id, Principal principal) {
	//     Board Board = boardService.getBoard(id);
	    
	//     if (Board == null) {
	//         throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity not found");
	//     }
	    
	//     SiteUser author = userService.getUserByEmail(principal.getName());
	    
	//     if (Board.getAuthor().getId() != author.getId()) {
	//     	throw new ResponseStatusException(HttpStatus.FORBIDDEN, "이 질문에 대한 권한이 없습니다.");
	//     }
	    
	//     boardService.updateResolve(Board);
	    
	//     return "redirect:/board/detail/" + Board.getId();
	// }
	
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/modify/{id}")
	public String BoardModify(BoardCreateForm boardCreateForm, @PathVariable("id") Integer id, Principal principal) {
		Board Board = this.boardService.getBoard(id);
		SiteUser author = userService.getUserByEmail(principal.getName());
	    
	    if (Board.getAuthor().getId() != author.getId()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
		}
	    boardCreateForm.setTitle(Board.getTitle());
	    boardCreateForm.setContent(Board.getContent());
		return "board/board_form";
		
	}
	
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/modify/{id}")
	public String BoardModify(@Valid BoardCreateForm boardCreateForm, BindingResult bindingResult, Principal principal, @PathVariable("id") Integer id) {
		if(bindingResult.hasErrors()) {
			return "Board_form";
		}
		Board Board = this.boardService.getBoard(id);
		SiteUser author = userService.getUserByEmail(principal.getName());
	    
	    if (Board.getAuthor().getId() != author.getId()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
		}
		this.boardService.modify(Board, boardCreateForm.getTitle(), boardCreateForm.getContent());
		return String.format("redirect:/board/detail/%s", id);
	}
	
	
	@GetMapping("/search")
	public String search(Model model, @RequestParam(value = "keyword", defaultValue = "") String keyword, @RequestParam(value = "page", defaultValue = "0") int page) {
	    Page<Board> Boards = boardService.searchBoards(page, keyword);
	    
	    Boards.getContent().forEach(Board -> {
	        if (Board.getCommentList() == null) {
	            Board.setCommentList(new ArrayList<>());
	        }
	    });
	    
	    model.addAttribute("paging", Boards);
	    model.addAttribute("keyword", keyword);
	    return "board/board_list";
	}
	
	
}
