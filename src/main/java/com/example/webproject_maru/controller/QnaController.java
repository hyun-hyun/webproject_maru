package com.example.webproject_maru.controller;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.webproject_maru.dto.CustomUserDetails;
import com.example.webproject_maru.dto.QnaPostDto;
import com.example.webproject_maru.service.QnaService;


@Controller
@RequestMapping("/qna")
public class QnaController {
    @Autowired
    private QnaService qnaService;

    @GetMapping
    public String getQuestionList(@AuthenticationPrincipal CustomUserDetails userDetails,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "15") int size,
        Model model
    ) {
        //미로그인시 에러처리
        String nickname;
        Long member_id=null;
        if(userDetails!=null){
            nickname = userDetails.member.getNickname();
            member_id = userDetails.member.getId();
            model.addAttribute("nickname", nickname);
            model.addAttribute("member_id", member_id);
        }else{
            //로그인 안된경우
            nickname="방문자";
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<QnaPostDto> questions = qnaService.getQuestionsWithNotice(pageable);
        
        // 현재 페이지 정보와 전체 페이지 정보를 모델에 추가
    model.addAttribute("questions", questions);
    model.addAttribute("currentPage", page);
    model.addAttribute("totalPages", questions.getTotalPages());

    // 페이지 네비게이션 관련 정보 추가
    model.addAttribute("isFirstPage", page == 0);
    model.addAttribute("isLastPage", page == questions.getTotalPages() - 1);

    // 이전 페이지와 다음 페이지 계산
    int previousPage = page > 0 ? page - 1 : 0;
    int nextPage = page < questions.getTotalPages() - 1 ? page + 1 : questions.getTotalPages() - 1;

    // 페이지 네비게이션에 사용할 값 추가
    model.addAttribute("previousPage", previousPage);
    model.addAttribute("nextPage", nextPage);

    // 페이지 번호 배열 추가
    List<Integer> pageNumbers = IntStream.range(0, questions.getTotalPages())
                                          .boxed()
                                          .collect(Collectors.toList());
    model.addAttribute("pageNumbers", pageNumbers);
        return "qnas/list";
    }

    @GetMapping("/search")
    public String searchQuestions(@AuthenticationPrincipal CustomUserDetails userDetails,
        @RequestParam String keyword,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "15") int size,
        Model model
    ) {
        //미로그인시 에러처리
        String nickname;
        Long member_id=null;
        if(userDetails!=null){
            nickname = userDetails.member.getNickname();
            member_id = userDetails.member.getId();
            model.addAttribute("nickname", nickname);
            model.addAttribute("member_id", member_id);
        }else{
            //로그인 안된경우
            nickname="방문자";
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<QnaPostDto> searchResults = qnaService.searchQuestions(keyword, pageable);
        model.addAttribute("questions", searchResults);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", searchResults.getTotalPages());

    // 페이지 네비게이션 관련 정보 추가
    model.addAttribute("isFirstPage", page == 0);
    model.addAttribute("isLastPage", page == searchResults.getTotalPages() - 1);

    // 이전 페이지와 다음 페이지 계산
    int previousPage = page > 0 ? page - 1 : 0;
    int nextPage = page < searchResults.getTotalPages() - 1 ? page + 1 : searchResults.getTotalPages() - 1;

    // 페이지 네비게이션에 사용할 값 추가
    model.addAttribute("previousPage", previousPage);
    model.addAttribute("nextPage", nextPage);

    // 페이지 번호 배열 추가
    List<Integer> pageNumbers = IntStream.range(0, searchResults.getTotalPages())
                                          .boxed()
                                          .collect(Collectors.toList());
    model.addAttribute("pageNumbers", pageNumbers);

        return "qnas/list";
    }

    // 상세 페이지
    @GetMapping("/{id}")
    public String getDetail(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long id, Model model) {
        //미로그인시 에러처리
        String nickname;
        Long member_id=null;
        if(userDetails!=null){
            nickname = userDetails.member.getNickname();
            member_id = userDetails.member.getId();
            model.addAttribute("nickname", nickname);
            model.addAttribute("member_id", member_id);
        }else{
            //로그인 안된경우
            nickname="방문자";
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iter = authorities.iterator();
        GrantedAuthority auth = iter.next();
        String role = auth.getAuthority();
        if(role.equals("ROLE_ADMIN") || role.equals("ROLE_MANAGER")){
            model.addAttribute("write", 1);
            model.addAttribute("edit", 1);
        }

        QnaPostDto post = qnaService.getPostDetail(id);
        List<QnaPostDto> replies = qnaService.getReplies(id);

        if(post.getAuthorNickname().equals(nickname)){
            model.addAttribute("edit", 1);
        }
        
        model.addAttribute("post", post);
        model.addAttribute("replies", replies);

        return "qnas/show";
    }

    // 글 작성 페이지
    @GetMapping("/create")
    public String createForm(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        String nickname = userDetails.member.getNickname();
        Long member_id = userDetails.member.getId();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iter = authorities.iterator();
        GrantedAuthority auth = iter.next();
        String role = auth.getAuthority();
        if(role.equals("ROLE_ADMIN") || role.equals("ROLE_MANAGER")){
            model.addAttribute("write", 1);
        }

        model.addAttribute("nickname", nickname);
        model.addAttribute("member_id", member_id);

        model.addAttribute("qnaPostDto", new QnaPostDto());
        return "qnas/new";
    }

    @PostMapping("/create")
    public String createPost(@ModelAttribute QnaPostDto qnaPostDto, @AuthenticationPrincipal CustomUserDetails userDetails) {
        qnaService.createPost(qnaPostDto, userDetails.getMember());
        return "redirect:/qna";
    }


    // 글 수정 페이지
    @GetMapping("/{id}/edit")
    public String editForm(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long id, Model model) {
        String nickname = userDetails.member.getNickname();
        Long member_id = userDetails.member.getId();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iter = authorities.iterator();
        GrantedAuthority auth = iter.next();
        String role = auth.getAuthority();
        if(role.equals("ROLE_ADMIN") || role.equals("ROLE_MANAGER")){
            model.addAttribute("write", 1);
        }

        model.addAttribute("nickname", nickname);
        model.addAttribute("member_id", member_id);

        QnaPostDto postDto = qnaService.getPostDetail(id);
        model.addAttribute("qnaPostDto", postDto);
        return "qnas/edit";
    }

    @PostMapping("/{id}/edit")
    public String editPost(@PathVariable Long id, @ModelAttribute QnaPostDto qnaPostDto) {
        qnaService.updatePost(id, qnaPostDto);
        return "redirect:/qna/" + id;
    }

    // 글 삭제
    @PostMapping("/{id}/delete")
    public String deletePost(@PathVariable Long id) {
        qnaService.deletePost(id);
        return "redirect:/qna";
    }

    // 답글 작성
    @PostMapping("/{id}/reply")
    public String addReply(@PathVariable Long id, QnaPostDto qnaPostDto, @AuthenticationPrincipal CustomUserDetails userDetails) {
        qnaService.createPost(qnaPostDto, userDetails.getMember());
        return "redirect:/qna/" + id;
    }

    // 답글 수정 페이지
    @GetMapping("/{id}/reply/{replyId}/edit")
    public String editReplyForm(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long id, @PathVariable Long replyId, Model model) {
        String nickname = userDetails.member.getNickname();
        Long member_id = userDetails.member.getId();

        model.addAttribute("nickname", nickname);
        model.addAttribute("member_id", member_id);

        QnaPostDto post = qnaService.getPostDetail(id);
        model.addAttribute("post", post);

        QnaPostDto reply = qnaService.getReplyDetail(replyId);
        model.addAttribute("reply", reply);
        model.addAttribute("postId", id);
        return "qnas/edit_r";
    }
    @PostMapping("/{id}/reply/{replyId}/edit")
    public String editReply(@PathVariable Long id, @PathVariable Long replyId, QnaPostDto qnaPostDto){
        qnaService.updatePost(replyId, qnaPostDto);
        return "redirect:/qna/" + id;
    }

    // 답글 삭제
    @PostMapping("/{id}/reply/{replyId}/delete")
    public String deleteReply(@PathVariable Long id, @PathVariable Long replyId) {
        qnaService.deleteReply(replyId);
        return "redirect:/qna/" + id;
    }
}
