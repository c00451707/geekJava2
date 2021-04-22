package io.hust.pony.demoweb.meta.repository;

import io.hust.pony.demoweb.meta.UserMetaSpec;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserSpecRepository extends JpaRepository<UserMetaSpec, Integer> {
    List<UserMetaSpec> findById(int id);
}
