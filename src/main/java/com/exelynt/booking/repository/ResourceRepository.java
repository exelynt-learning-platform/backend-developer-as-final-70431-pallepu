package com.exelynt.booking.repository;

import com.exelynt.booking.domain.Resource;
import com.exelynt.booking.domain.ResourceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {
    List<Resource> findByType(ResourceType type);
}
