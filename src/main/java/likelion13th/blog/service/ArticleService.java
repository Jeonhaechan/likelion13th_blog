package likelion13th.blog.service;
//
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import likelion13th.blog.domain.Article;
import likelion13th.blog.dto.*;
import likelion13th.blog.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.NoSuchElementException;
//
//@Service
//public class ArticleService {
//    private final List<Article> articleDB = new ArrayList<>();
//    private Long nextId = 1L;
//
//    public Article addArticle(Article article){
//        if(article.getAuthor()==null
//            || article.getContent() == null
//            || article.getTitle() == null
//            || article.getPassword() == null){
//            throw new IllegalArgumentException("제목, 내용, 작성자 , 비밀번호는 필수 입력 항목입니다.");
//        }
//
//
//        Article newArticle = new Article(
//                nextId++,
//                article.getContent(),
//                article.getTitle(),
//                article.getAuthor(),
//                article.getPassword()
//        );
//        articleDB.add(newArticle);
//
//        return newArticle;
//    }
//
//    public List<Article> findAll(){
//        //임시 db반환
//        return articleDB;
//    }
//    public Article findById(Long id){
//        for(Article article: articleDB){
//            if(article.getId().equals(id)){
//                return article;
//            }
//        }
//        throw new NoSuchElementException("해당 ID의 게시글을 찾을 수 없습니다.");
//    }
//}
@Service
@RequiredArgsConstructor
public class ArticleService {
    private final ArticleRepository articleRepository;

    public ArticleResponse addArticle(AddArticleRequest request){
        //request 객체의 .toEntity()를 통해 Article 객체 생성
        Article article=request.toEntity();

        //Article객체를 JPA의 save() 를 사용하여 DB에 저장
        articleRepository.save(article);

        // article 객체를 response DTO 생성하여 반환
        //  response 클래스의 정작 팩토리 메서드 of() 통해 API 응답객체 생성
        return ArticleResponse.of(article);
    }

    public List<SimpleArticleResponse> getAllArticles(){
        //findAll()
        List<Article> articleList = articleRepository.findAll();
        List<SimpleArticleResponse> articleResponseList = articleList.stream().map(article -> SimpleArticleResponse.of(article)).toList();
        return articleResponseList;
    }
    public ArticleResponse getArticle(Long id){
        /* 1. JPA의 findById()를 사용하여 DB에서 id가 일치하는 게시글 찾기.
              id가 일치하는 게시글이 DB에 없으면 에러 반환*/
        Article article=articleRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("해당 ID의 게시글을 찾을 수 없습니다. ID: "+id));

        /*2. ArticleResponse DTO 생성하여 반환 */
        return ArticleResponse.of(article);
    }
    //글 수정
    @Transactional
    public ArticleResponse updateArticle(Long id, UpdateArticleRequest request) {
        Article article=articleRepository.findById(id)
                .orElseThrow(()->new EntityNotFoundException("해당 ID의 게시글을 찾으 수 없습니다"));


        if(!article.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("해당 글에 대한 수정 권한이 없습니다.");
        }

        article.update(request.getTitle(), request.getContent());
        article=articleRepository.save(article);

        return ArticleResponse.of(article);


    }
    //글 삭제
    @Transactional
    public void deleteArticle(Long id, DeleteRequest request){
        Article article=articleRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("해당 ID의 게시글을 찾을 수 없습니다."));

        if(!request.getPassword().equals(article.getPassword())) {
            throw new RuntimeException("해당 글에 대한 삭제 권한이 없습니다.");
        }

        articleRepository.deleteById(id);

    }


}