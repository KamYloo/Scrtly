package com.kamylo.Scrtly_backend.service;

import com.kamylo.Scrtly_backend.exception.CommentException;
import com.kamylo.Scrtly_backend.exception.PostException;
import com.kamylo.Scrtly_backend.exception.UserException;
import com.kamylo.Scrtly_backend.model.Comment;
import com.kamylo.Scrtly_backend.model.Like;
import com.kamylo.Scrtly_backend.model.Post;
import com.kamylo.Scrtly_backend.model.User;
import com.kamylo.Scrtly_backend.repository.CommentRepository;

import com.kamylo.Scrtly_backend.repository.LikeRepository;
import com.kamylo.Scrtly_backend.repository.PostRepository;
import com.kamylo.Scrtly_backend.request.SendCommentRequest;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
    @Autowired
    private LikeRepository likeRepository;

    @Override
    public Comment createComment(SendCommentRequest sendCommentRequest) throws UserException, PostException {
        User user = userService.findUserById(sendCommentRequest.getUser().getId());
        Optional<Post> post = postRepository.findById(sendCommentRequest.getPostId());

        Comment comment = new Comment();
        comment.setUser(user);
        comment.setPost(post.get());
        comment.setComment(sendCommentRequest.getComment());
        Comment createdComment = commentRepository.save(comment);
        post.get().getComments().add(createdComment);
        postRepository.save(post.get());
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
        Like like = new Like();
        like.setUser(user);
        like.setComment(comment);
        likeRepository.save(like);
       comment.getLikes().add(like);

       return commentRepository.save(comment);
    }

    @Override
    public Comment unLikeComment(Long commentId, Long userId) throws CommentException, UserException {
        User user = userService.findUserById(userId);
        Comment comment = findCommentById(commentId);
        Like like = new Like();
        like.setUser(user);
        like.setComment(comment);
        likeRepository.save(like);
        comment.getLikes().remove(like);

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
