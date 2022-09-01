package com.example.intermediate.controller;


import com.example.intermediate.controller.response.ResponseDto;
import com.example.intermediate.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@RestController
public class LikeController {

    private final LikeService likeService;

    @RequestMapping(value = "/api/auth/like/post/{id}", method = RequestMethod.POST)
    public ResponseDto<?> postLike(@PathVariable Long id, HttpServletRequest request) {
        return likeService.postLike(id,request);
    }

    @RequestMapping(value = "/api/auth/like/post/{id}", method = RequestMethod.DELETE)
    public ResponseDto<?> postDisLike(@PathVariable Long id, HttpServletRequest request) {
        return likeService.postDisLike(id,request);
    }

    @RequestMapping(value = "/api/auth/like/comment/{id}", method = RequestMethod.POST)
    public ResponseDto<?> commentLike(@PathVariable Long id, HttpServletRequest request) {
        return likeService.commentLike(id,request);
    }

    @RequestMapping(value = "/api/auth/like/comment/{id}", method = RequestMethod.DELETE)
    public ResponseDto<?> commentDisLike(@PathVariable Long id, HttpServletRequest request) {
        return likeService.commentDisLike(id,request);
    }

    @RequestMapping(value = "/api/auth/like/commentReply/{id}", method = RequestMethod.POST)
    public ResponseDto<?> commentReplyLike(@PathVariable Long id, HttpServletRequest request) {
        return likeService.commentReplyLike(id,request);
    }

    @RequestMapping(value = "/api/auth/like/commentReply/{id}", method = RequestMethod.DELETE)
    public ResponseDto<?> commentReplyDisLike(@PathVariable Long id, HttpServletRequest request) {
        return likeService.commentReplyDisLike(id,request);
    }

}
