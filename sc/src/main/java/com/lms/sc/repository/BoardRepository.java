package com.lms.sc.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lms.sc.entity.Board;
import com.lms.sc.entity.SiteUser;



public interface BoardRepository extends JpaRepository<Board, Integer> {
    // 'Question'을 'Board'로 변경
    @Query("SELECT DISTINCT b FROM Board b LEFT JOIN FETCH b.commentList WHERE b.author = :author ORDER BY b.createDate DESC")
    Page<Board> findByAuthor(@Param("author") SiteUser author, Pageable pageable);

    // Video와 Lecture 관련 메서드는 Board 엔티티에 해당 필드가 없다면 제거 또는 수정이 필요합니다.
    // 예: List<Board> findByAuthorAndVideo_Lecture(SiteUser author, Lecture lecture);

    @Query("SELECT b FROM Board b WHERE b.author = :author ORDER BY b.createDate DESC")
    List<Board> findTop3ByAuthor(@Param("author") SiteUser author, Pageable pageable);

    @Query("SELECT b FROM Board b WHERE b.title LIKE %:keyword% OR b.content LIKE %:keyword% OR b.author.name LIKE %:keyword%")
    Page<Board> searchQuestions(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT DISTINCT b FROM Board b LEFT JOIN FETCH b.commentList WHERE b.title LIKE %:keyword% OR b.content LIKE %:keyword%")
    Page<Board> findByKeywordWithAnswers(@Param("keyword") String keyword, Pageable pageable);

    void deleteAllByAuthor(SiteUser author);
    
    // Video와 Lecture 관련 메서드 수정 또는 제거
    // @Query("SELECT b FROM Board b WHERE b.author = :author AND b.video.lecture = :lecture ORDER BY b.createDate DESC")
    // List<Board> findByAuthorAndLectureOrderByCreateDateAsc(@Param("author") SiteUser author, @Param("lecture") Lecture lecture);
}
