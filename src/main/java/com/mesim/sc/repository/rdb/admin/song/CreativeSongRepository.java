package com.mesim.sc.repository.rdb.admin.song;

import com.mesim.sc.repository.rdb.admin.AdminRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CreativeSongRepository extends AdminRepository<CreativeSong, String> {

    Page<CreativeSong> findAll(Specification<Object> specification, Pageable pageable);

    List<CreativeSong> findAllByGenre(String genre);


}
