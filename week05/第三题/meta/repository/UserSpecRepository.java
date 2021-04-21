package io.hust.pony.demoweb.meta.repository;

import io.hust.pony.demoweb.meta.UserMetaSpec;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSpecRepository extends JpaRepository<UserMetaSpec, Integer> {
}
