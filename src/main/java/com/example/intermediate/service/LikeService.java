package com.example.intermediate.service;

import com.example.intermediate.controller.response.ResponseDto;
import com.example.intermediate.domain.*;
import com.example.intermediate.jwt.TokenProvider;
import com.example.intermediate.repository.*;

import javax.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class LikeService {

    private final CommentReplyRepository commentReplyRepository;
    private final CommentReplyLikeRepository commentReplyLikeRepository;
    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final TokenProvider tokenProvider;

    @Transactional
    public ResponseDto<?> postLike(Long postId, HttpServletRequest request) {
        if (null == request.getHeader("Refresh-Token")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
        }

        Post post = isPresentPost(postId);
        if (null == post) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
        }

//        Optional<PostLike> postLikeList = postLikeRepository.findByPostId(postId);
        if(postLikeRepository.findByPostId(postId).isEmpty()){
            PostLike postLike = PostLike.builder()
                    .postId(postId)
                    .member(member)
                    .flag(true)
                    .build();

            post.like();
            postLikeRepository.save(postLike);
        }else{
            return ResponseDto.fail("already like", "already like state");
        }
        return ResponseDto.success("like success");
    }

    @Transactional
    public ResponseDto<?> postDisLike(Long postId, HttpServletRequest request) {
        if (null == request.getHeader("Refresh-Token")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
        }

        Post post = isPresentPost(postId);
        if (null == post) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
        }
        PostLike postLike = isPresentPostLike(postId);
        if (postLike.validateMember(member)) {
            return ResponseDto.fail("dislike fail", "좋아요 작성자가 아닙니다.");
        }

//        Optional<PostLike> postLikeList = postLikeRepository.findByPostId(postId);
        if(postLike.isFlag() == false){
            return ResponseDto.fail("already dislike", "already dislike state");
        }else{
            postLike.setFlag(false);
            post.dislike();
            postLikeRepository.delete(postLike);
        }
        return ResponseDto.success("dislike success");
    }

    @Transactional
    public ResponseDto<?> commentLike(Long commentId, HttpServletRequest request) {
        if (null == request.getHeader("Refresh-Token")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
        }

        Comment comment = isPresentComment(commentId);
        if (null == comment) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 코멘트 id 입니다.");
        }

        if(commentLikeRepository.findByCommentId(commentId).isEmpty()){
            CommentLike commentLike = CommentLike.builder()
                    .commentId(commentId)
                    .member(member)
                    .flag(true)
                    .build();

            comment.like();
            commentLikeRepository.save(commentLike);
        }else{
            return ResponseDto.fail("already like", "already like state");
        }
        return ResponseDto.success("like success");
    }

    @Transactional
    public ResponseDto<?> commentDisLike(Long commentId, HttpServletRequest request) {
        if (null == request.getHeader("Refresh-Token")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
        }

        Comment comment = isPresentComment(commentId);
        if (null == comment) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 코멘트 id 입니다.");
        }

        CommentLike commentLike = isPresentCommentLike(commentId);
        if (commentLike.validateMember(member)) {
            return ResponseDto.fail("dislike fail", "좋아요 작성자가 아닙니다.");
        }

        Optional<CommentLike> commentLikeList = commentLikeRepository.findByCommentId(commentId);
        if(commentLikeList.get().isFlag() == true){
            comment.dislike();
            commentLikeRepository.delete(commentLike);
        }else{
            return ResponseDto.fail("already dislike", "already dislike state");
        }
        return ResponseDto.success("dislike success");
    }

    @Transactional
    public ResponseDto<?> commentReplyLike(Long commentReplyId, HttpServletRequest request) {
        if (null == request.getHeader("Refresh-Token")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
        }

        CommentReply commentReply = isPresentCommentReply(commentReplyId);
        if (null == commentReply) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 대댓글 id 입니다.");
        }

        if(commentReplyLikeRepository.findByCommentReplyId(commentReplyId).isEmpty()){
            CommentReplyLike commentReplyLike = CommentReplyLike.builder()
                    .commentReplyId(commentReplyId)
                    .member(member)
                    .flag(true)
                    .build();

            commentReply.like();
            commentReplyLikeRepository.save(commentReplyLike);
        }else{
            return ResponseDto.fail("already like", "already like state");
        }
        return ResponseDto.success("like success");
    }

    @Transactional
    public ResponseDto<?> commentReplyDisLike(Long commentReplyId, HttpServletRequest request) {
        if (null == request.getHeader("Refresh-Token")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
        }

        CommentReply commentReply = isPresentCommentReply(commentReplyId);
        if (null == commentReply) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 대댓글 id 입니다.");
        }

        CommentReplyLike commentReplyLike = isPresentCommentReplyLike(commentReplyId);
        if (commentReplyLike.validateMember(member)) {
            return ResponseDto.fail("dislike fail", "좋아요 작성자가 아닙니다.");
        }

        Optional<CommentReplyLike> commentReplyLikeList = commentReplyLikeRepository.findByCommentReplyId(commentReplyId);
        if(commentReplyLikeList.get().isFlag() == true){
            commentReply.dislike();
            commentReplyLikeRepository.delete(commentReplyLike);
        }else{
            return ResponseDto.fail("already dislike", "already dislike state");
        }
        return ResponseDto.success("dislike success");
    }

    @Transactional(readOnly = true)
    public CommentReply isPresentCommentReply(Long id) {
        Optional<CommentReply> optionalCommentReply = commentReplyRepository.findById(id);
        return optionalCommentReply.orElse(null);
    }

    @Transactional(readOnly = true)
    public CommentReplyLike isPresentCommentReplyLike(Long id) {
        Optional<CommentReplyLike> optionalCommentReplyLike = commentReplyLikeRepository.findById(id);
        return optionalCommentReplyLike.orElse(null);
    }

    @Transactional(readOnly = true)
    public Comment isPresentComment(Long id) {
        Optional<Comment> optionalComment = commentRepository.findById(id);
        return optionalComment.orElse(null);
    }

    @Transactional(readOnly = true)
    public CommentLike isPresentCommentLike(Long id) {
        Optional<CommentLike> optionalCommentLike = commentLikeRepository.findById(id);
        return optionalCommentLike.orElse(null);
    }

    @Transactional(readOnly = true)
    public Post isPresentPost(Long id) {
        Optional<Post> optionalPost = postRepository.findById(id);
        return optionalPost.orElse(null);
    }

    @Transactional(readOnly = true)
    public PostLike isPresentPostLike(Long id) {
        Optional<PostLike> optionalPostLike = postLikeRepository.findById(id);
        return optionalPostLike.orElse(null);
    }

    @Transactional
    public Member validateMember(HttpServletRequest request) {
        if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
            return null;
        }
        return tokenProvider.getMemberFromAuthentication();
    }
}