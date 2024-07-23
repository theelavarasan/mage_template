package org.mage.data_validator.repository;

import org.mage.data_validator.entity.TemplateData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TemplateDataRepository extends JpaRepository<TemplateData, Long> {
}
