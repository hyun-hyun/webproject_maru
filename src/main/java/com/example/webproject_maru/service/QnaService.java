package com.example.webproject_maru.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.webproject_maru.dto.QnaPostDto;
import com.example.webproject_maru.entity.Member;
import com.example.webproject_maru.entity.Qna;
import com.example.webproject_maru.repository.MemberRepository;
import com.example.webproject_maru.repository.QnaRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QnaService {
    @Autowired
    private QnaRepository qnaRepository;


    // 글 작성
    @Transactional
    public void createPost(QnaPostDto postDTO, Member author) {
        Qna post = new Qna();
        post.setTitle(postDTO.getTitle());
        post.setContent(postDTO.getContent());
        post.setAuthor(author);

        if (postDTO.getParentId() != null) { // 답글인 경우
            Qna parentPost = qnaRepository.findById(postDTO.getParentId())
                .orElseThrow(() -> new IllegalArgumentException("원글을 찾을 수 없습니다."));
            post.setParentPost(parentPost);
        }else{
            post.setNotice(postDTO.isNotice());
        }

        qnaRepository.save(post);
    }

    // 공지사항을 먼저 조회하고 그 후 일반 질문들을 조회
    public Page<QnaPostDto> getQuestionsWithNotice(Pageable pageable) {
        // 공지사항을 먼저 조회
        Page<Qna> noticePosts = qnaRepository.findNoticePosts(pageable);
        // 일반 질문들
        Page<QnaPostDto> normalPosts = qnaRepository.findAllByParentPostIsNullAndIsNoticeFalseOrderByAppendTimeDesc(pageable)
                .map(this::mapToDTO);

        // 공지사항과 일반 질문 결합
        List<QnaPostDto> posts = new ArrayList<>();

        posts.addAll(noticePosts.stream().map(this::mapToDTO).collect(Collectors.toList()));
        posts.addAll(normalPosts.getContent());

        return new PageImpl<>(posts, pageable, noticePosts.getTotalElements() + normalPosts.getTotalElements());
    }


    // 검색
    public Page<QnaPostDto> searchQuestions(String keyword, Pageable pageable) {
        return qnaRepository.searchByTitleOrAuthor(keyword, pageable)
            .map(this::mapToDTO);
    }

    // 답글 가져오기
    public List<QnaPostDto> getReplies(Long parentId) {
        return qnaRepository.findAllByParentPostIdOrderByAppendTimeAsc(parentId)
            .stream()
            .map(this::mapToDTO)
            .toList();
    }

    // 상세 글 조회
    public QnaPostDto getPostDetail(Long id) {
        Qna post = qnaRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("글을 찾을 수 없습니다."));
        return mapToDTO(post);
    }

    // 글 수정
    @Transactional
    public void updatePost(Long id, QnaPostDto qnaPostDto) {
        Qna post = qnaRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("글을 찾을 수 없습니다."));
        post.setTitle(qnaPostDto.getTitle());
        post.setContent(qnaPostDto.getContent());
        if (qnaPostDto.getParentId() != null) { // 답글인 경우
            Qna parentPost = qnaRepository.findById(qnaPostDto.getParentId())
                .orElseThrow(() -> new IllegalArgumentException("원글을 찾을 수 없습니다."));
            post.setParentPost(parentPost);
        }else{
            post.setNotice(qnaPostDto.isNotice());
        }
        qnaRepository.save(post);
    }

    // 글 삭제
    @Transactional
    public void deletePost(Long id) {
        Qna post = qnaRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("글을 찾을 수 없습니다."));
        
        // 답글이 있는지 확인
        if (post.getReplies() == null || post.getReplies().isEmpty()) {
            // 답글이 없으면 글을 완전히 삭제
            qnaRepository.delete(post);
        } else {
            // 답글이 있으면 삭제 상태로만 변경
            post.setDeleted(true);
            qnaRepository.save(post);
        }
    }

    //답글수정
    @Transactional
    public QnaPostDto getReplyDetail(Long replyId) {
        Qna reply = qnaRepository.findById(replyId)
            .orElseThrow(() -> new IllegalArgumentException("해당 답글을 찾을 수 없습니다. ID: " + replyId));
        return mapToDTO(reply);
    }

    @Transactional
    public void updateReply(Long replyId, String content) {
        Qna reply = qnaRepository.findById(replyId)
            .orElseThrow(() -> new IllegalArgumentException("해당 답글을 찾을 수 없습니다. ID: " + replyId));
        reply.setContent(content);
        qnaRepository.save(reply);
    }

    // 답글 삭제
    public void deleteReply(Long replyId) {
        qnaRepository.deleteById(replyId); // 완전 삭제
    }

    private QnaPostDto mapToDTO(Qna post) {
        QnaPostDto dto = new QnaPostDto();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setAuthorNickname(post.getAuthor().getNickname());
        dto.setAppendTime(post.getAppendTime());
        dto.setNotice(post.isNotice());
        dto.setDeleted(post.isDeleted());
        dto.setAnswered(!post.getReplies().isEmpty());
        return dto;
    }
}
