package com.citypulse.report.repository;

import com.citypulse.report.entity.Report;
import com.citypulse.report.entity.ReportStatus;
import com.citypulse.report.entity.ReportSubjectType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReportRepository extends JpaRepository<Report, UUID> {

    Page<Report> findByStatus(ReportStatus status, Pageable pageable);

    long countByStatus(ReportStatus status);

    Page<Report> findBySubjectTypeAndSubjectId(ReportSubjectType subjectType, UUID subjectId, Pageable pageable);

    @Query("SELECT r FROM Report r WHERE r.reporterId = :reporterId AND r.subjectType = :subjectType AND r.subjectId = :subjectId AND r.status = 'OPEN'")
    Optional<Report> findActiveReportByReporterAndSubject(
        @Param("reporterId") UUID reporterId,
        @Param("subjectType") ReportSubjectType subjectType,
        @Param("subjectId") UUID subjectId
    );
}
