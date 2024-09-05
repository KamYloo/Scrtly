package com.kamylo.Scrtly_backend.service;

import com.kamylo.Scrtly_backend.exception.CommentException;
import com.kamylo.Scrtly_backend.exception.PostException;
import com.kamylo.Scrtly_backend.exception.UserException;
import com.kamylo.Scrtly_backend.model.Comment;
import com.kamylo.Scrtly_backend.model.Post;
import com.kamylo.Scrtly_backend.model.User;
import com.kamylo.Scrtly_backend.repository.CommentRepository;

import com.kamylo.Scrtly_backend.repository.PostRepository;
import com.kamylo.Scrtly_backend.request.SendCommentRequest;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImplementation implements CommentService{

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private PostService postService;
    @Autowired
    private UserService userService;
    @Autowired
    private PostRepository postRepository;

    @Override
    public Comment createComment(SendCommentRequest sendCommentRequest) throws UserException, PostException {
        User user = userService.findUserById(sendCommentRequest.getUser().getId());
        Post post = postService.findPostById(sendCommentRequest.getPostId());
        Comment comment = new Comment();
        comment.setUser(user);
        comment.setPost(post);
        comment.setComment(sendCommentRequest.getComment());
        Comment createdComment = commentRepository.save(comment);
        post.getComments().add(createdComment);
        postRepository.save(post);
        return createdComment;
    }

    @Override
    public Comment findCommentById(Long commentId) throws CommentException {
        return commentRepository.findById(commentId).orElseThrow(() -> new CommentException("Comment not found with id " + commentId));
    }

    @Override
    public List<Comment> getAllCommentsByPostId(Long postId) throws PostException {
        return commentRepository.findCommentByPostId(postId);
    }

    @Override
    public Comment likeComment(Long commentId, Long userId) throws CommentException, UserException {
       User user = userService.findUserById(userId);
       Comment comment = findCommentById(commentId);
       comment.getLikes().add(user);

       return commentRepository.save(comment);
    }

    @Override
    public Comment unLikeComment(Long commentId, Long userId) throws CommentException, UserException {
        User user = userService.findUserById(userId);
        Comment comment = findCommentById(commentId);
        comment.getLikes().remove(user);

        return commentRepository.save(comment);
    }

    @Override
    public void deleteComment(Long commentId, Long userId) throws CommentException, UserException {
        Comment comment = findCommentById(commentId);

        if (!userId.equals(comment.getUser().getId())) {
            throw new UserException("You are not authorized to delete this comment");
        }

        commentRepository.deleteById(commentId);
    }
}
