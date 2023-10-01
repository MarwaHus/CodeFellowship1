package com.CodeFellowship.CodeFellowship.repository;

import com.CodeFellowship.CodeFellowship.models.AppUser;
import com.CodeFellowship.CodeFellowship.models.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface PostRepository extends JpaRepository<Post,Long> {
    List<Post> findAllByAppUser(AppUser appUser);
}
