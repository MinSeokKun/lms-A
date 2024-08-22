package com.lms.sc.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.lms.sc.entity.Lecture;
import com.lms.sc.entity.SiteUser;
import com.lms.sc.entity.Video;
import com.lms.sc.service.LectureService;
import com.lms.sc.service.UserLectureService;
import com.lms.sc.service.UserService;
import com.lms.sc.service.VideoService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
	
	private final UserService userService;
	private final VideoService videoService;
	private final LectureService lectureService;
	private final UserLectureService userLecService;
	
	//유저 정보가기
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/user/list")
	public String getUserList(Model model, @RequestParam(value = "kw", defaultValue = "") String kw, @RequestParam(value = "page", defaultValue = "0") int page) {
	    Page<SiteUser> paging = userService.getList(page, kw);
	    model.addAttribute("paging", paging);
	    model.addAttribute("kw", kw);
	    return "admin/userinfo";
	}
	
	//전체 비디오 가지고오기
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/video/list")
	public String allList(Model model, Principal principal) {
		List<Video> vidList = videoService.allList();
		
		model.addAttribute("vidList", vidList);
		
		return "admin/allVidList";
	}
	
	//강의 리스트 이동
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/lecture/list")
	public String lecList(Model model, Principal principal) {
		List<Lecture> lecture = lectureService.lecList();
		model.addAttribute("lecture", lecture);

		return "admin/lec_list";
	}
	
	//강의 등록 이동
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/lecture/regist")
	public String regLectureForm() {
		return "admin/lec_register";
	}
	
	//강의 등록
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/lecture/regist")
	public String regist(@RequestParam("title") String title,
						@RequestParam("content") String content,
						@RequestParam(value = "thumnailUrl", required = false) MultipartFile thumnailUrl,
						Principal principal) throws Exception {
//			Lecture lecture = lectureService.getLecture(id);
		String email = principal.getName();
		Lecture lecture = lectureService.regLecture(title, content, email);
		
		if (thumnailUrl != null && !thumnailUrl.isEmpty()) {
			lectureService.updatethumnail(lecture.getId(), thumnailUrl);
		}
		return "redirect:/admin/lecList";
	}
	
	//강의 수정페이지 이동
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/lecture/modify/{id}")
	public String modify(Model model, @PathVariable("id") long id) throws Exception {
		Lecture lecture = lectureService.getLecture(id);
		
		model.addAttribute("lecture", lecture);
		return "admin/lec_modify";
	}
	
	//강의 수정
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/lecture/modify/{id}")
	public String modify(@PathVariable("id") long id, 
					@RequestParam("title") String title,
					@RequestParam("content") String content,
					@RequestParam(value = "thumnailUrl", required = false) MultipartFile thumnailUrl) throws Exception {
		Lecture lecture = lectureService.getLecture(id);
		
		
		lectureService.modify(lecture, title, content);
		
		if (thumnailUrl != null && !thumnailUrl.isEmpty()) {
			lectureService.updatethumnail(lecture.getId(), thumnailUrl);
		}
		return "redirect:/admin/lecture/list";
	}
		
	// 강의 삭제
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/lecture/remove/{id}")
	public String removeLec(@PathVariable("id") long id) throws Exception {
		Lecture lecture = lectureService.getLecture(id);
		lectureService.remove(lecture);
		return "redirect:/admin/lecture/list";
	}
	
	//강의 마다 영상 등록 페이지로 이동
	@GetMapping("/video/addVideo/{id}")
	public String addVideo(Model model, @PathVariable("id") long id) throws Exception {
		Lecture lecture = lectureService.getLecture(id);
		
		model.addAttribute("lecture", lecture);
		
		return "admin/video_form";
	}
	
	//비디오 등록
	@PostMapping("/video/addVideo/{lec_id}")
	public String regVideo(@PathVariable("lec_id") long lec_id,
							@RequestParam(name = "title[]") String[] title, @RequestParam(name = "url[]") String[] url) throws Exception {

		//VideoService.regVideo(title, url, lec_id);
		for (int i = 0; i < title.length; i++) {
	        videoService.regVideo(title[i], url[i], lec_id);
	    }
		
		return String.format("redirect:/admin/video/list/%s", lec_id);
	}
	
	//등록된 영상 리스트
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/video/list/{lec_id}")
	public String videoList(Model model, @PathVariable("lec_id") long lec_id) throws Exception {
		Lecture lecture = lectureService.getLecture(lec_id);		
		List<Video> video = videoService.VideoList(lecture);
		model.addAttribute("lecture", lecture);
		model.addAttribute("video", video);
		return "admin/video_list";
	}
	
	//비디오 삭제
	@PreAuthorize("hasRole('ADMIN')") // 관리자만 삭제할 수 있도록 설정
	@GetMapping("/video/delete/{id}")
    public String deleteVideo(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) throws Exception {
		Video video = videoService.getVideo(id);
		long lecId = video.getLecture().getId();
		try {
            videoService.deleteVideo(id);
            
            System.out.println(redirectAttributes.addFlashAttribute("message", "비디오가 성공적으로 삭제되었습니다."));
            
        } catch (EntityNotFoundException e) {
        	System.out.println(redirectAttributes.addFlashAttribute("error", e.getMessage()));
        } catch (Exception e) {
        	System.out.println(redirectAttributes.addFlashAttribute("error", "비디오 삭제 중 오류가 발생했습니다."));
        }
        return "redirect:/admin/video/list/" + lecId;  // 비디오 목록 페이지로 리다이렉트
    }
}
