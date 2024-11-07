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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LikeServiceImplementation implements LikeService{

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentRepository commentRepository;

    @Override
    public Like likePost(Long postId, User user) throws UserException, PostException {
       Like islikeExistPost = likeRepository.isLikeExistPost(user.getId(), postId);

       if (islikeExistPost != null) {
           likeRepository.deleteById(islikeExistPost.getId());
           return islikeExistPost;
       }

        Post post = postRepository.findById(postId).get();
       Like like = new Like();
       like.setUser(user);
       like.setPost(post);
       Like savedLike = likeRepository.save(like);
       post.getLikes().add(savedLike);
       postRepository.save(post);
       return savedLike;
    }

    @Override
    public Like likeComment(Long commentId, User user) throws UserException, CommentException {
       Like islikeExistComment = likeRepository.isLikeExistComment(user.getId(), commentId);

       if (islikeExistComment != null) {
           likeRepository.deleteById(islikeExistComment.getId());
           return islikeExistComment;
       }

       Comment comment = commentService.findCommentById(commentId);
       System.out.println("Wazne" + comment.getComment());
       Like like = new Like();
       like.setUser(user);
       like.setComment(comment);
       Like savedLike = likeRepository.save(like);
       comment.getLikes().add(savedLike);
       commentRepository.save(comment);
       return savedLike;
    }

    @Override
    public List<Like> getLikesByPost(Long postId) throws PostException {
       Optional<Post> post = postRepository.findById(postId);
       if (post.isPresent()) {
           return likeRepository.findByPostId(post.get().getId());
       }
       throw new PostException("Post not found with id: " + postId);
    }

    @Override
    public List<Like> getLikesByComment(Long commentId) throws CommentException {
       Comment comment = commentService.findCommentById(commentId);
       if (comment != null) {
           return likeRepository.findByCommentId(commentId);
       }
      throw new CommentException("Comment not found with id: " + commentId);
    }
}
