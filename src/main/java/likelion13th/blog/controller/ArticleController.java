package likelion13th.blog.controller;
import likelion13th.blog.domain.Article;
import likelion13th.blog.dto.*;
import likelion13th.blog.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.ArrayList;
//import java.util.List;
//
//
//@RestController
//@RequestMapping("/articles")
//public class ArticleController {
//    private final ArticleService articleService;
//    public ArticleController(ArticleService articleService){
//        this.articleService = articleService;
//    }
//
//
//    @PostMapping
//    public ResponseEntity<Article> createArticle(@RequestBody Article article){
//        Article newArticle = articleService.addArticle(article);
//
//        return ResponseEntity.status(HttpStatus.CREATED).body(newArticle);
//    }
//    @GetMapping
//    public ResponseEntity<List<Article>> getArticle(){
//
//        List<Article> articles =articleService.findAll();
//
//        return ResponseEntity.status(HttpStatus.OK).body(articles);
//    }
//    @GetMapping("/{id}")
//    public ResponseEntity<Article> getArticle(@PathVariable Long id){
//        Article article = articleService.findById(id);
//
//        return ResponseEntity.status(HttpStatus.OK).body(article);
//    }
//
//}
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/articles")
public class ArticleController {
    private final ArticleService articleService;

    /*게시글 추가*/
    @PostMapping
    public ResponseEntity<ApiResponse> createArticle(@RequestBody AddArticleRequest request){
        ArticleResponse response=articleService.addArticle(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(true,201,"게시글 등록 성공",response));

    }
    /*게시글 전체 조회*/
    @GetMapping
    public ResponseEntity<ApiResponse> ReadAllArticles(){

        List<SimpleArticleResponse> articles=articleService.getAllArticles();
        return ResponseEntity.ok(new ApiResponse(true, 200, "게시글 조회 성공", articles));

    }

    /*게시글 단일 조회*/
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> ReadArticle(@PathVariable long id){
        ArticleResponse response=articleService.getArticle(id);
        return ResponseEntity.ok(new ApiResponse(true,200,"게시글 조회 성공", response));

    }
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateArticle(@PathVariable long id,
                                                     @RequestBody UpdateArticleRequest request){
        ArticleResponse response= articleService.updateArticle(id,request);
        return ResponseEntity.ok(new ApiResponse(true,204,"게시글 수정 성공",response));

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteArticle(@PathVariable long id, @RequestBody DeleteRequest request){

        articleService.deleteArticle(id,request);
        return ResponseEntity.ok(new ApiResponse(true,204,"게시글 삭제 성공"));
    }

}