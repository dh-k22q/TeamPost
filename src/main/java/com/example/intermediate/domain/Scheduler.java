package com.example.intermediate.domain;

import com.example.intermediate.repository.CommentRepository;
import com.example.intermediate.repository.PostRepository;
import com.example.intermediate.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;

@Slf4j
@RequiredArgsConstructor// final 멤버 변수를 자동으로 생성합니다.
@Component // 스프링이 필요 시 자동으로 생성하는 클래스 목록에 추가합니다.
public class Scheduler {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PostService postService;

    // 초, 분, 시, 일, 월, 주 순서
    //매일 AM 01:00 마다 댓글이 0개인 게시물 전체 삭제하기
    @Scheduled(cron = " 0 0 1 * * *")
    public void updatePostByComment() {
        log.info("게시글 업데이트 실행");

        // 저장된 모든 댓글 조회
        List<Post> postList = postRepository.findAll();
        if (postList.size() != 0) {
            for (long id = postList.get(0).getId(); id <= postList.get(postList.size() - 1).getId(); id++) {
                Post postId = postService.isPresentPost(id);
                if (postId != null) {
                    List<Comment> commentCount = commentRepository.findAllByPost(postId);
                    if (commentCount.size() == 0) {
                        log.info("게시물 " + postRepository.findById(id).get().getTitle() + "이 삭제되었습니다.");
                        postRepository.deleteById(id);
                    }
                }
            }
        }

    }


}
