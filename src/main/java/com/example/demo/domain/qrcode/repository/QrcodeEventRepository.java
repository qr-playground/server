package com.example.demo.domain.qrcode.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.domain.qrcode.entity.QrcodeEvent;
import com.example.demo.domain.user.entity.User;

public interface QrcodeEventRepository extends JpaRepository<QrcodeEvent, UUID> {

  Optional<QrcodeEvent> findByShortId(String shortId);

  Page<QrcodeEvent> findAllByUserAndIsDeletedFalse(User user, Pageable pageable);

  @Query(value = """
      SELECT qe
        FROM QrcodeEvent qe
        LEFT JOIN FETCH qe.qrcodeBenefit
        LEFT JOIN FETCH qe.qrcodeDesign
       WHERE qe.user      = :user
         AND qe.isDeleted = false
      """, countQuery = "SELECT COUNT(qe) FROM QrcodeEvent qe WHERE qe.user = :user AND qe.isDeleted = false")

  Page<QrcodeEvent> findAllByUserWithDetails(@Param("user") User user, Pageable pageable);

  @Query(value = """
      SELECT DISTINCT qe
      FROM QrcodeEvent qe
      LEFT JOIN FETCH qe.qrcodeBenefit
      LEFT JOIN FETCH qe.qrcodeDesign
      WHERE qe.isDeleted  = false
      AND qe.title LIKE CONCAT('%', :keyword, '%')
      ORDER BY qe.createdAt DESC
              """, countQuery = """
      SELECT COUNT(DISTINCT qe)
      FROM QrcodeEvent qe
      WHERE qe.isDeleted = false
      AND qe.title LIKE CONCAT('%', :keyword, '%')
              """)
  Page<QrcodeEvent> searchQrcodeEvents(@Param("keyword") String keyword, Pageable pageable);

  @Query(value = """
      SELECT *
        FROM qrcode_event
       WHERE search_text &@* :keyword
         AND is_deleted = false
       ORDER BY created_at DESC
      """, countQuery = """
      SELECT COUNT(*)
        FROM qrcode_event
       WHERE search_text &@* :keyword
         AND is_deleted = false
      """, nativeQuery = true)
  Page<QrcodeEvent> searchQrcodeEventsPgroonga(@Param("keyword") String keyword, Pageable pageable);

  @Query(value = """
        SELECT *
          FROM qrcode_event qe
         WHERE qe.search_text &`(
                 'fuzzy_search('
                 || 'search_text, '
                 || pgroonga_escape(:keyword)
                 || ', 1)'
               )
           AND qe.is_deleted = false
      ORDER BY
        pgroonga_score(qe.tableoid, qe.ctid) DESC,
        qe.created_at DESC
        """,
      // countQuery 는 동일 WHERE 절로 전체 카운트
      countQuery = """
          SELECT COUNT(*)
            FROM qrcode_event qe
           WHERE qe.search_text &`(
                   'fuzzy_search('
                   || 'search_text, '
                   || pgroonga_escape(:keyword)
                   || ', 2)'
                 )
             AND qe.is_deleted = false
          """, nativeQuery = true)
  Page<QrcodeEvent> searchFuzzy(
      @Param("keyword") String keyword,
      Pageable pageable);

}
