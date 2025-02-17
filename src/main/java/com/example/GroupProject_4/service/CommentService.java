package com.example.GroupProject_4.service;

import com.example.GroupProject_4.exception.ResourceNotFoundException;
import com.example.GroupProject_4.exception.UserPermissionException;
import com.example.GroupProject_4.model.entity.CommentEntity;
import com.example.GroupProject_4.repository.CommentRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Objects;

@AllArgsConstructor
@Service
public class CommentService {

    private CommentRepository commentRepository;

    public CommentEntity createComment(CommentEntity commentEntity) {
        return commentRepository.save(commentEntity);
    }

    public Page<CommentEntity> getAllComment(Integer pageNumber, Integer pageSize, Sort.Direction direction, String sortBy) {
        return commentRepository.findAll(PageRequest.of(pageNumber, pageSize, Sort.by(direction, sortBy)));
    }

    public Page<CommentEntity> getAllCommentsByPostId(Long postId, Integer pageNumber, Integer pageSize, Sort.Direction direction, String sortBy) {
        return commentRepository.findAllCommentsByPostId(postId, PageRequest.of(pageNumber, pageSize, Sort.by(direction, sortBy)));
    }

    public CommentEntity getCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment with id %d was not found".formatted(commentId)));
    }

    public String deleteComment(Long userId, Long commentId) {
        CommentEntity commentById = getCommentById(commentId);
        if (!Objects.equals(commentById.getOwner().getId(), userId))
            throw new UserPermissionException("User With id %d doesn't have permission for this action".formatted(userId));
        commentRepository.delete(commentById);
        return "Comment with id %d was deleted".formatted(commentId);
    }

    public void deleteComment(Long commentId) {
        CommentEntity commentById = getCommentById(commentId);
        commentRepository.delete(commentById);
        System.out.printf("Comment with id %d was deleted%n", commentId);
    }

    @Transactional
    public CommentEntity editComment(Long commentId, Long userId, CommentEntity commentEntity) {
        CommentEntity commentToEdit = getCommentById(commentId);
        if (!Objects.equals(commentToEdit.getOwner().getId(), userId))
            throw new UserPermissionException("User With id %d doesn't have permission for this action".formatted(userId));
        commentToEdit.setText(commentEntity.getText());
        return commentToEdit;
    }
}
