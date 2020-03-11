package main.repo;

import main.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends CrudRepository<User, Integer> {
    User findByUsername(String name);

    User findByActivationCode(String code);
}
