package com.CodeFellowship.CodeFellowship.repository;

import com.CodeFellowship.CodeFellowship.models.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUser,Long> {
    AppUser findByUsername(String username);


}
