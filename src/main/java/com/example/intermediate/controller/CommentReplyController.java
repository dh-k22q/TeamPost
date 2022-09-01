package com.example.intermediate.controller;


import com.example.intermediate.controller.request.CommentReplyRequestDto;
import com.example.intermediate.controller.response.ResponseDto;
import com.example.intermediate.service.CommentReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Validated
@RequiredArgsConstructor
@RestController
public class CommentReplyController {
    private final CommentReplyService commentReplyService;

    @RequestMapping(value = "/api/auth/commentReply", method = RequestMethod.POST)
    public ResponseDto<?> createCommentReply(@RequestBody CommentReplyRequestDto requestDto,
                                             HttpServletRequest request) {
        return commentReplyService.createCommentReply(requestDto, request);
    }

    @RequestMapping(value = "/api/commentReply/{id}", method = RequestMethod.GET)
    public ResponseDto<?> getAllCommentReply(@PathVariable Long id) {
        return commentReplyService.getAllCommentReplysByComment(id);
    }


    @RequestMapping(value = "/api/auth/commentReply/{id}", method = RequestMethod.DELETE)
    public ResponseDto<?> deleteCommentReply(@PathVariable Long id,
                                             HttpServletRequest request) {
        return commentReplyService.deleteCommentReply(id, request);
    } @RequestMapping(value = "/api/auth/commentReply/{id}", method = RequestMethod.PUT)
    public ResponseDto<?> updateCommentReply(@PathVariable Long id, @RequestBody CommentReplyRequestDto requestDto,
                                             HttpServletRequest request) {
        return commentReplyService.updateCommentReply(id, requestDto, request);
    }
}