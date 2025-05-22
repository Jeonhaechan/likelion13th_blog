package likelion13th.blog.repository;

import likelion13th.blog.domain.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

//@Repository
@Repository
public interface ArticleRepository extends JpaRepository<Article,Long> {

    // 특정 id값보다 큰 id값을 가지고, 제목을 포함하는 Article 객체 전체 반환
    @Query("SELECT a FROM Article a WHERE a.id> :id")
    List<Article> getAllGreaterThan(@Param("id") Long id);
}