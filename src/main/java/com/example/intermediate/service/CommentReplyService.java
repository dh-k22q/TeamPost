package com.example.intermediate.service;

import com.example.intermediate.controller.request.CommentReplyRequestDto;
import com.example.intermediate.controller.response.CommentReplyResponseDto;
import com.example.intermediate.controller.response.CommentResponseDto;
import com.example.intermediate.controller.response.ResponseDto;
import com.example.intermediate.domain.Comment;
import com.example.intermediate.domain.CommentReply;
import com.example.intermediate.domain.Member;
import com.example.intermediate.jwt.TokenProvider;
import com.example.intermediate.repository.CommentReplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentReplyService {
    private final CommentReplyRepository commentReplyRepository;

    private final TokenProvider tokenProvider;
    private final CommentService commentService;

    @Transactional
    public ResponseDto<?> createCommentReply(CommentReplyRequestDto requestDto, HttpServletRequest request) {
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

        Comment comment = commentService.isPresentComment(requestDto.getCommentId());
        if (null == comment) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 코멘트 id 입니다.");
        }

        CommentReply commentreply = CommentReply.builder()
                .member(member)
                .comment(comment)
                .likeNum(0)
                .content(requestDto.getContent())
                .build();
        commentReplyRepository.save(commentreply);
        return ResponseDto.success(
                CommentResponseDto.builder()
                        .id(commentreply.getId())
                        .author(commentreply.getMember().getNickname())
                        .content(commentreply.getContent())
                        .likeNum(commentreply.getLikeNum())
                        .createdAt(commentreply.getCreatedAt())
                        .modifiedAt(commentreply.getModifiedAt())
                        .build()
        );
    }

    @Transactional(readOnly = true)
    public ResponseDto<?> getAllCommentReplysByComment(Long commentId) {
        Comment comment = commentService.isPresentComment(commentId);
        if (null == comment) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 코멘트 id 입니다.");
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
        return ResponseDto.success(commentReplyResponseDtoList);
    }

    @Transactional
    public ResponseDto<?> updateCommentReply(Long id, CommentReplyRequestDto requestDto, HttpServletRequest request) {
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

        Comment comment = commentService.isPresentComment(requestDto.getCommentId());
        if (null == comment) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 코멘트 id 입니다.");
        }

        CommentReply commentreply = isPresentCommentReply(id);
        if (null == commentreply) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 대댓글 id 입니다.");
        }

        if (comment.validateMember(member)) {
            return ResponseDto.fail("BAD_REQUEST", "작성자만 수정할 수 있습니다.");
        }

        commentreply.update(requestDto);
        return ResponseDto.success(
                CommentResponseDto.builder()
                        .id(commentreply.getId())
                        .author(commentreply.getMember().getNickname())
                        .content(commentreply.getContent())
                        .likeNum(comment.getLikeNum())
                        .createdAt(commentreply.getCreatedAt())
                        .modifiedAt(commentreply.getModifiedAt())
                        .build()
        );
    }

    @Transactional
    public ResponseDto<?> deleteCommentReply(Long id, HttpServletRequest request) {
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

        CommentReply commentreply = isPresentCommentReply(id);
        if (null == commentreply) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 대댓글 id 입니다.");
        }

        if (commentreply.validateMember(member)) {
            return ResponseDto.fail("BAD_REQUEST", "작성자만 수정할 수 있습니다.");
        }

        commentReplyRepository.delete(commentreply);
        return ResponseDto.success("success");
    }

    @Transactional(readOnly = true)
    public CommentReply isPresentCommentReply(Long id) {
        Optional<CommentReply> optionalCommentreply = commentReplyRepository.findById(id);
        return optionalCommentreply.orElse(null);
    }

    @Transactional
    public Member validateMember(HttpServletRequest request) {
        if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
            return null;
        }
        return tokenProvider.getMemberFromAuthentication();
    }
}