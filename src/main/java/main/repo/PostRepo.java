package main.repo;

import main.model.Post;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepo extends CrudRepository<Post, Integer> {

    List<Post> findByTag(String tag);
}
