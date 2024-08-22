package com.lms.sc.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.lms.sc.entity.Lecture;
import com.lms.sc.entity.Video;
import com.lms.sc.service.LectureService;
import com.lms.sc.service.VideoService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class User2Controller {
	
	private final VideoService videoService;
	private final LectureService lectureService;
	
	  //전체 비디오 가지고오기
  	@PreAuthorize("isAuthenticated()")
  	@GetMapping("/video/list")
  	public String allList(Model model, Principal principal) {
  		List<Video> vidList = videoService.allList();
  		
  		model.addAttribute("vidList", vidList);
  		
  		return "user/video/list";
  	}
  	
  //강의 리스트 이동
  	@PreAuthorize("isAuthenticated()")
  	@GetMapping("/lecture/list")
  	public String lecList(Model model, Principal principal) {
  		List<Lecture> lecture = lectureService.lecList();
  		model.addAttribute("lecture", lecture);

  		return "user/lecture/list";
  	}
}
