package com.lms.sc.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import com.lms.sc.createForm.CommentCreateForm;
import com.lms.sc.entity.Comment;
import com.lms.sc.entity.Board;
import com.lms.sc.entity.SiteUser;
import com.lms.sc.exception.DataNotFoundException;
import com.lms.sc.service.CommentService;
import com.lms.sc.service.BoardService;
import com.lms.sc.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequestMapping("/comment")
@RequiredArgsConstructor
@Controller
public class CommentController {
	
	private final BoardService boardService;
	private final CommentService commentService;
	private final UserService userService;	
	
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/create/{id}")
	public String createAnswer(Model model, @PathVariable("id") Integer id,
	@Valid CommentCreateForm commentCreateForm, BindingResult bindingResult, Principal principal) {
		SiteUser user = userService.getUserByEmail(principal.getName());
		Board board = this.boardService.getBoard(id);
		if (bindingResult.hasErrors()) {
			model.addAttribute("board", board);
			List<Comment> commentList = commentService.getAnswerList(board);
			model.addAttribute("answerList", commentList);
			return "board/board_detail";
		}
		this.commentService.create(board, commentCreateForm.getContent(), user);
		return String.format("redirect:/board/detail/%s", id);
	}
	
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/modify/{answerId}")
	public String answerModify(@Valid CommentCreateForm answerCreateForm, BindingResult bindingResult, 
			Principal principal, @PathVariable("answerId") Integer answerId) {
		if(bindingResult.hasErrors()) {
			return "Board_detail{id}";
		}
		Comment answer = this.commentService.getAnswer(answerId);
		if(!answer.getAuthor().getEmail().equals(principal.getName())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
		}
		this.commentService.modify(answer, answerCreateForm.getContent());
		return String.format("redirect:/board/detail/%s", answer.getBoard().getId());
	}
	
	
	 @GetMapping("/delete/{id}") 
	 public String answerDelete(Model model, @PathVariable("id") Integer id) {
		Comment answer = this.commentService.getAnswer(id); 
		if (answer == null) {
			throw new DataNotFoundException("Answer not found"); 
		} 
		commentService.delete(answer);
	 
		return String.format("redirect:/board/detail/%s", answer.getBoard().getId());
	}
	 
}
