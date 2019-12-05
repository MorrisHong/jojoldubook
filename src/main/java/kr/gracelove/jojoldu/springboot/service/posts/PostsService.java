package kr.gracelove.jojoldu.springboot.service.posts;

import kr.gracelove.jojoldu.springboot.domain.posts.Posts;
import kr.gracelove.jojoldu.springboot.domain.posts.PostsRepository;
import kr.gracelove.jojoldu.springboot.web.dto.PostsResponseDto;
import kr.gracelove.jojoldu.springboot.web.dto.PostsSaveRequestDto;
import kr.gracelove.jojoldu.springboot.web.dto.PostsUpdateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PostsService {
    private final PostsRepository postsRepository;

    @Transactional
    public Long save(PostsSaveRequestDto requestDto) {
        return postsRepository.save(requestDto.toEntity()).getId();
    }

    @Transactional
    public Long update(Long id, PostsUpdateRequestDto requestDto) {
        Posts posts = postsRepository.findById(id).orElseThrow( () -> new IllegalArgumentException("해당 사용자가 없습니다. id="+id));
        posts.update(requestDto.getTitle(), requestDto.getContent());
        return id;
    }

    public PostsResponseDto findById(Long id) {
        Posts entity = postsRepository.findById(id).orElseThrow( () -> new IllegalArgumentException("해당 사용자가 없습니다. id="+id));
        return new PostsResponseDto(entity);
    }
}
