package com.yo.yotion.domain.page.repository;

import com.yo.yotion.domain.page.entity.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PageRepository extends JpaRepository <Page, Long> {
    List<Page> findByUser_Id(Long userId);
}
