package com.example.intermediate.service;

import com.example.intermediate.controller.response.ResponseDto;
import com.example.intermediate.controller.response.CommentResponseDto;
import com.example.intermediate.domain.Comment;
import com.example.intermediate.domain.Member;
import com.example.intermediate.domain.Post;
import com.example.intermediate.controller.request.CommentRequestDto;
import com.example.intermediate.jwt.TokenProvider;
import com.example.intermediate.repository.CommentRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.intermediate.controller.response.CommentReplyResponseDto;
import com.example.intermediate.controller.response.ResponseDto;
import com.example.intermediate.controller.response.CommentResponseDto;
import com.example.intermediate.domain.Comment;
import com.example.intermediate.domain.CommentReply;
import com.example.intermediate.domain.Member;
import com.example.intermediate.domain.Post;
import com.example.intermediate.controller.request.CommentRequestDto;
import com.example.intermediate.jwt.TokenProvider;
import com.example.intermediate.repository.CommentReplyRepository;
import com.example.intermediate.repository.CommentRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

  private final CommentRepository commentRepository;

  private final CommentReplyRepository commentReplyRepository;

  private final TokenProvider tokenProvider;
  private final PostService postService;

  @Transactional
  public ResponseDto<?> createComment(CommentRequestDto requestDto, HttpServletRequest request) {
    if (null == request.getHeader("Refresh-Token")) {
      return ResponseDto.fail("MEMBER_NOT_FOUND",
              "???????????? ???????????????.");
    }

    if (null == request.getHeader("Authorization")) {
      return ResponseDto.fail("MEMBER_NOT_FOUND",
              "???????????? ???????????????.");
    }

    Member member = validateMember(request);
    if (null == member) {
      return ResponseDto.fail("INVALID_TOKEN", "Token??? ???????????? ????????????.");
    }

    Post post = postService.isPresentPost(requestDto.getPostId());
    if (null == post) {
      return ResponseDto.fail("NOT_FOUND", "???????????? ?????? ????????? id ?????????.");
    }

    Comment comment = Comment.builder()
            .member(member)
            .post(post)
            .likeNum(0)
            .content(requestDto.getContent())
            .build();
    commentRepository.save(comment);
    return ResponseDto.success(
            CommentResponseDto.builder()
                    .id(comment.getId())
                    .author(comment.getMember().getNickname())
                    .content(comment.getContent())
                    .likeNum(comment.getLikeNum())
                    .createdAt(comment.getCreatedAt())
                    .modifiedAt(comment.getModifiedAt())
                    .build()
    );
  }

  @Transactional(readOnly = true)
  public ResponseDto<?> getAllCommentsByPost(Long postId) {
    Comment comment = isPresentComment(postId);
    if (null == comment) {
      return ResponseDto.fail("NOT_FOUND", "???????????? ?????? ????????? id ?????????.");
    }

    List<CommentReply> commentReplyList = commentReplyRepository.findAllByComment(comment);
    List<CommentReplyResponseDto> commentReplyResponseDtoList = new ArrayList<>();

    for (CommentReply commentreply : commentReplyList) {
      commentReplyResponseDtoList.add(
              CommentReplyResponseDto.builder()
                      .id(commentreply.getId())
                      .author(commentreply.getMember().getNickname())
                      .content(commentreply.getContent())
                      .likeNum(commentreply.getLikeNum())
                      .createdAt(commentreply.getCreatedAt())
                      .modifiedAt(commentreply.getModifiedAt())
                      .build()
      );
    }
    return ResponseDto.success(CommentResponseDto.builder()
            .id(comment.getId())
            .author(comment.getMember().getNickname())
            .content(comment.getContent())
            .likeNum(comment.getLikeNum())
            .commentResponseReplyDtoList(commentReplyResponseDtoList)
            .createdAt(comment.getCreatedAt())
            .modifiedAt(comment.getModifiedAt())
            .build()
    );
  }

  @Transactional
  public ResponseDto<?> updateComment(Long id, CommentRequestDto requestDto, HttpServletRequest request) {
    if (null == request.getHeader("Refresh-Token")) {
      return ResponseDto.fail("MEMBER_NOT_FOUND",
              "???????????? ???????????????.");
    }

    if (null == request.getHeader("Authorization")) {
      return ResponseDto.fail("MEMBER_NOT_FOUND",
              "???????????? ???????????????.");
    }

    Member member = validateMember(request);
    if (null == member) {
      return ResponseDto.fail("INVALID_TOKEN", "Token??? ???????????? ????????????.");
    }

    Post post = postService.isPresentPost(requestDto.getPostId());
    if (null == post) {
      return ResponseDto.fail("NOT_FOUND", "???????????? ?????? ????????? id ?????????.");
    }

    Comment comment = isPresentComment(id);
    if (null == comment) {
      return ResponseDto.fail("NOT_FOUND", "???????????? ?????? ?????? id ?????????.");
    }

    if (comment.validateMember(member)) {
      return ResponseDto.fail("BAD_REQUEST", "???????????? ????????? ??? ????????????.");
    }

    comment.update(requestDto);
    return ResponseDto.success(
            CommentResponseDto.builder()
                    .id(comment.getId())
                    .author(comment.getMember().getNickname())
                    .content(comment.getContent())
                    .likeNum(comment.getLikeNum())
                    .createdAt(comment.getCreatedAt())
                    .modifiedAt(comment.getModifiedAt())
                    .build()
    );
  }

  @Transactional
  public ResponseDto<?> deleteComment(Long id, HttpServletRequest request) {
    if (null == request.getHeader("Refresh-Token")) {
      return ResponseDto.fail("MEMBER_NOT_FOUND",
              "???????????? ???????????????.");
    }

    if (null == request.getHeader("Authorization")) {
      return ResponseDto.fail("MEMBER_NOT_FOUND",
              "???????????? ???????????????.");
    }

    Member member = validateMember(request);
    if (null == member) {
      return ResponseDto.fail("INVALID_TOKEN", "Token??? ???????????? ????????????.");
    }

    Comment comment = isPresentComment(id);
    if (null == comment) {
      return ResponseDto.fail("NOT_FOUND", "???????????? ?????? ?????? id ?????????.");
    }

    if (comment.validateMember(member)) {
      return ResponseDto.fail("BAD_REQUEST", "???????????? ????????? ??? ????????????.");
    }

    commentRepository.delete(comment);
    return ResponseDto.success("success");
  }

  @Transactional(readOnly = true)
  public Comment isPresentComment(Long id) {
    Optional<Comment> optionalComment = commentRepository.findById(id);
    return optionalComment.orElse(null);
  }

  @Transactional
  public Member validateMember(HttpServletRequest request) {
    if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
      return null;
    }
    return tokenProvider.getMemberFromAuthentication();
  }
}