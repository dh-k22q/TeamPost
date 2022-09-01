package com.example.intermediate.repository;

import com.example.intermediate.domain.CommentReplyLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentReplyLikeRepository extends JpaRepository<CommentReplyLike, Long> {
    Optional<CommentReplyLike> findByCommentReplyId(Long commentReplyId);

}
