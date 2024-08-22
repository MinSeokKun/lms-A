package com.lms.sc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.lms.sc.entity.Comment;
import com.lms.sc.entity.Notice;
import com.lms.sc.entity.Board;
import com.lms.sc.entity.SiteUser;
import com.lms.sc.repository.CommentRepository;
import com.lms.sc.repository.NoticeRepository;
import com.lms.sc.repository.BoardRepository;
import com.lms.sc.repository.UserRepository;
import com.lms.sc.service.NoticeService;
import com.lms.sc.service.BoardService;
import com.lms.sc.service.UserService;

@SpringBootTest
class ScApplicationTests {

	@Autowired
	private BoardRepository questionRepository;
	
	@Autowired
	private CommentRepository AnswerRepository;
	
	@Autowired
	private UserRepository UserRepository;
	
	@Autowired
	private UserService userservice;
	
	@Autowired
	private BoardService questionService;
	
	@Autowired
	private NoticeRepository noticeRepository;
	
	@Autowired
	private NoticeService noticeService;

	@Test
	void test8() {
		for (int i = 1; i <= 2; i++) {
			String title = String.format("테스트 데이터:[%03d]", i);
			String content = "내용 없음";
			SiteUser author = userservice.getUserByEmail("admin");
			this.noticeService.create(title, content, author);
		}
	}
	
	//@Test
	void test7() {
		Notice n1 = new Notice();
		n1.setTitle("공지사항");
		n1.setContent("~~~~");
		n1.setCreateDate(LocalDateTime.now());
		this.noticeRepository.save(n1);
	}
	
	//@Test
	void userCreate() {
		String username = "민석";
		String email = "minseok";
		String password = "1234";
//		userservice.create(username, email, password);
	}
	
	//@Test
	void test() {
		Board q1 = new Board();
		q1.setTitle("청력");
		q1.setContent("이 안 좋은데 자막기능도 있나요?");
		q1.setCreateDate(LocalDateTime.now());
		this.questionRepository.save(q1);
		
		Board q2 = new Board();
		q2.setTitle("시력");
		q2.setContent("이 안 좋은데 강의화면을 확대할 수 있나요?");
		q2.setCreateDate(LocalDateTime.now());
		this.questionRepository.save(q2);
	}
	
	//@Test
	void test2() {
		assertEquals(2, this.questionRepository.count());
		Optional<Board> oq = this.questionRepository.findById(1);
		assertTrue(oq.isPresent());
		Board q = oq.get();
		this.questionRepository.delete(q);
		assertEquals(1, this.questionRepository.count());
	}
	
	
//	@Test
	void test3() {
		Optional<Board> oq = this.questionRepository.findById(53);
		assertTrue(oq.isPresent());
		Board q = oq.get();
		
//		Answer a1 = new Answer();
//		a1.setContent("123.");
//		a1.setQuestion(q);
//		a1.setCreateDate(LocalDateTime.now());
//		this.AnswerRepository.save(a1);
		
		Comment a2 = new Comment();
		a2.setContent("모니터 큰걸로 사용하세요.");
		a2.setBoard(q);
		a2.setCreateDate(LocalDateTime.now());
		this.AnswerRepository.save(a2);
	}
	//@Test
	void test4() {
		SiteUser u = new SiteUser();
		String name="청력";
		String email="user@naver.com";
		String password="1234";
		String tellNumber="01012345678";
		String profileImage=null;
		userservice.create(name, email, password, tellNumber, profileImage);
	}
//	@Test
		void test5() {
			Board q1 = new Board();
			q1.setTitle("아아아");
			q1.setContent("이이dfdfdfdf이이이이");
			q1.setCreateDate(LocalDateTime.now());
			this.questionRepository.save(q1);
	}

 // @Test
		void test6() {
			for (int i = 1; i <= 2; i++) {
				String subject = String.format("테스트 데이터:[%03d]", i);
				String content = "내용 없음";
				SiteUser author = userservice.getUserByEmail("admin");
				this.questionService.create(subject, content, author);
			}
	}
}
