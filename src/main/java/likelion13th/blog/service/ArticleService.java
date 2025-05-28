package likelion13th.blog.service;
//
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import likelion13th.blog.domain.Article;
import likelion13th.blog.dto.*;
import likelion13th.blog.repository.ArticleRepository;
import likelion13th.blog.repository.CommentRepository;
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
    private final CommentRepository commentRepository;

    //글 추가
    @Transactional
    public ArticleResponse addArticle(AddArticleRequest request){

        /*1. Article 객체 생성*/
        Article article=request.toEntity();

        /*2. 레포지토리에 저장*/
        article=articleRepository.save(article);

        /*3. ArticleResponse DTO 생성하여 반환*/
        return ArticleResponse.of(article);

    }


    //전체 글 조회
    public List<SimpleArticleResponse> getAllArticles(){
        /*1. 데이터베이스에 저장된 전체 게시글 목록 가져오기*/
        List<Article> articleList = articleRepository.findAll();

        /*2. Article -> ArticleResponse : 엔티티를 DTO로 변환*/
        List<SimpleArticleResponse> articleResponseList=articleList.stream()
                .map(article -> SimpleArticleResponse.of(article))
                .toList();

        /*3. articleResponseList (DTO 리스트) 반환 */
        return articleResponseList;

    }


    //단일 글 조회
    public ArticleDetailResponse getArticle(Long id){
        /* 1. 요청이 들어온 게시글 ID로 데이터베이스에서 게시글 찾기. 해당하는 게시글이 없으면 에러*/
        Article article=articleRepository.findById(id)
                .orElseThrow(()->new EntityNotFoundException("해당 ID의 게시글을 찾을 수 없습니다."));

        /*2. 해당 게시글에 달려있는 댓글들 가져오기*/
        List<CommentResponse> comments=getCommentList(article);

        /*3. ArticleResponse DTO 생성하여 반환 */
        return ArticleDetailResponse.of(article,comments);
    }


    //글 삭제
    @Transactional
    public void deleteArticle(Long id, DeleteRequest request){
        /* 1. 요청이 들어온 게시글 ID로 데이터베이스에서 게시글 찾기. 해당하는 게시글이 없으면 에러*/
        Article article=articleRepository.findById(id)
                .orElseThrow(()->new EntityNotFoundException("해당 ID의 게시글을 찾을 수 없습니다."));

        /*2. 비밀번호 일치하는지 확인 : 요청을 보낸 사람이 이 게시글의 삭제권한을 가지고 있는지
            request.getPassword() : 게시글 수정 요청을 보낸 사람이 입력한 비밀번호
            article.getPassword() : 데이터베이스에 저장된 비밀번호 (작성자가 글 쓸때 등록한)
         */
        if(!request.getPassword().equals(article.getPassword())){
            throw new RuntimeException("해당 글에 대한 삭제 권한이 없습니다.");
        }


        /*3. 게시글 삭제 */
        articleRepository.deleteById(id);

    }

    //글 수정
    @Transactional
    public ArticleResponse updateArticle(Long id, UpdateArticleRequest request){

        /* 1. 요청이 들어온 게시글 ID로 데이터베이스에서 게시글 찾기. 해당하는 게시글이 없으면 에러*/
        Article article=articleRepository.findById(id)
                .orElseThrow(()->new EntityNotFoundException("해당 ID의 게시글을 찾을 수 없습니다."));


        /*2. 비밀번호 일치하는지 확인 : 요청을 보낸 사람이 이 게시글의 수정 권한을 가지고 있는지
            request.getPassword() : 게시글 수정 요청을 보낸 사람이 입력한 비밀번호
            article.getPassword() : 데이터베이스에 저장된 비밀번호 (작성자가 글 쓸때 등록한)
         */
        if(!article.getPassword().equals(request.getPassword())){
            throw new RuntimeException("해당 글에 대한 수정 권한이 없습니다.");
        }

        /*3. 게시글 수정 후 저장 */
        article.update(request.getTitle(),request.getContent());

        /* ArticleResponse로 변환해서 리턴 */
        return ArticleResponse.of(article);

    }


    //특정 게시글에 달려있는 댓글목록 가져오기
    private List<CommentResponse> getCommentList(Article article){
        return commentRepository.findByArticle(article).stream()
                .map(comment->CommentResponse.of(comment))
                .toList();
    }


}