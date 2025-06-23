package com.example.blog.controller;

import com.example.blog.model.BlogPost;
import com.example.blog.service.BlogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BlogController.class)
public class BlogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BlogService blogService;

    @Autowired
    private ObjectMapper objectMapper;

    private BlogPost testPost;

    @BeforeEach
    void setUp() {
        testPost = new BlogPost("Test Title", "Test Content", "Test Author");
        testPost.setId(1L);
    }

    @Test
    void testGetAllPosts() throws Exception {
        when(blogService.getAllPosts()).thenReturn(Arrays.asList(testPost));
        mockMvc.perform(get("/api/blog/posts"))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$[0].title").value("Test Title"));
    }

    @Test
    void testGetPostById() throws Exception {
        when(blogService.getPostById(1L)).thenReturn(Optional.of(testPost));
        mockMvc.perform(get("/api/blog/posts/1"))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.title").value("Test Title"));
    }

    @Test
    void testCreatePost() throws Exception {
        when(blogService.createPost(any(BlogPost.class))).thenReturn(testPost);
        mockMvc.perform(post("/api/blog/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testPost)))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.title").value("Test Title"));
    }

    @Test
    void testUpdatePost() throws Exception {
        when(blogService.updatePost(anyLong(), any(BlogPost.class))).thenReturn(testPost);
        mockMvc.perform(put("/api/blog/posts/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testPost)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.title").value("Test Title"));
    }

    @Test
    void testDeletePost() throws Exception {
        when(blogService.deletePost(1L)).thenReturn(true);
        mockMvc.perform(delete("/api/blog/posts/1"))
               .andExpect(status().isNoContent());
    }
}
