package kr.gracelove.jojoldu.springboot.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.gracelove.jojoldu.springboot.domain.posts.Posts;
import kr.gracelove.jojoldu.springboot.domain.posts.PostsRepository;
import kr.gracelove.jojoldu.springboot.web.dto.PostsSaveRequestDto;
import kr.gracelove.jojoldu.springboot.web.dto.PostsUpdateRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Slf4j
public class PostsApiControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private PostsRepository postsRepository;

    @Autowired
    private MockMvc mvc;

    @After
    public void tearDown() throws Exception {
        postsRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles="USER")
    public void Posts_등록된다() throws Exception {
        //given
        String title = "title";
        String content = "content";
        PostsSaveRequestDto requestDto = PostsSaveRequestDto.builder()
                .title(title)
                .content(content)
                .author("author")
                .build();

        String url = "http://localhost:"+port+"/api/v1/posts";

        //when
        mvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isOk());

        //then
        List<Posts> all = postsRepository.findAll();
        assertThat(all.get(0).getTitle()).isEqualTo(title);
        assertThat(all.get(0).getContent()).isEqualTo(content);
     }

     @Test
     @WithMockUser(roles="USER")
     public void post수정된다() throws Exception {
         //given
         Posts savePosts = postsRepository.save(Posts.builder()
                 .title("title")
                 .content("content")
                 .author("author")
                 .build());

         Long updateId = savePosts.getId();
         String expectedTitle = "updatedTitle";
         String expectedContent = "updatedContent";

         PostsUpdateRequestDto requestDto = PostsUpdateRequestDto.builder()
                 .title(expectedTitle)
                 .content(expectedContent)
                 .build();

         String url = "http://localhost:"+port+"/api/v1/posts/"+updateId;

         HttpEntity<PostsUpdateRequestDto> requestEntity = new HttpEntity<>(requestDto);

         //when
         ResponseEntity<Long> responseEntity = restTemplate.exchange(url, HttpMethod.PUT, requestEntity, Long.class);

         //when
         mvc.perform(put(url)
                 .contentType(MediaType.APPLICATION_JSON_UTF8)
                 .content(new ObjectMapper().writeValueAsString(requestDto)))
                 .andExpect(status().isOk());

         //then
         List<Posts> all = postsRepository.findAll();
         assertThat(all.get(0).getTitle()).isEqualTo(expectedTitle);
         assertThat(all.get(0).getContent()).isEqualTo(expectedContent);
     }

}