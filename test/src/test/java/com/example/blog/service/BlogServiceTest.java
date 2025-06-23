package com.example.blog.service;

import com.example.blog.model.BlogPost;
import com.example.blog.repository.BlogPostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BlogServiceTest {

    @Mock
    private BlogPostRepository blogPostRepository;

    @InjectMocks
    private BlogService blogService;

    private BlogPost testPost;

    @BeforeEach
    void setUp() {
        testPost = new BlogPost("Test Title", "Test Content", "Test Author");
        testPost.setId(1L);
    }

    @Test
    void testGetAllPosts() {
        when(blogPostRepository.findAll()).thenReturn(Arrays.asList(testPost));
        List<BlogPost> result = blogService.getAllPosts();
        assertEquals(1, result.size());
        assertEquals("Test Title", result.get(0).getTitle());
        verify(blogPostRepository, times(1)).findAll();
    }

    @Test
    void testGetPostById() {
        when(blogPostRepository.findById(1L)).thenReturn(Optional.of(testPost));
        Optional<BlogPost> result = blogService.getPostById(1L);
        assertTrue(result.isPresent());
        assertEquals("Test Title", result.get().getTitle());
        verify(blogPostRepository, times(1)).findById(1L);
    }

    @Test
    void testCreatePost() {
        when(blogPostRepository.save(any(BlogPost.class))).thenReturn(testPost);
        BlogPost result = blogService.createPost(testPost);
        assertNotNull(result);
        assertEquals("Test Title", result.getTitle());
        verify(blogPostRepository, times(1)).save(any(BlogPost.class));
    }

    @Test
    void testUpdatePost() {
        BlogPost updatedPost = new BlogPost("Updated Title", "Updated Content", "Updated Author");
        when(blogPostRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(blogPostRepository.save(any(BlogPost.class))).thenReturn(updatedPost);

        BlogPost result = blogService.updatePost(1L, updatedPost);
        assertNotNull(result);
        assertEquals("Updated Title", result.getTitle());
        verify(blogPostRepository, times(1)).findById(1L);
        verify(blogPostRepository, times(1)).save(any(BlogPost.class));
    }

    @Test
    void testDeletePost() {
        when(blogPostRepository.existsById(1L)).thenReturn(true);
        doNothing().when(blogPostRepository).deleteById(1L);
        boolean result = blogService.deletePost(1L);
        assertTrue(result);
        verify(blogPostRepository, times(1)).deleteById(1L);
    }
}
