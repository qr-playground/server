package com.example.demo.domain.statistic.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.domain.statistic.entity.Statistic;

public interface StatisticRepository extends JpaRepository<Statistic, UUID> {
    @Modifying
    @Query(value = """
            INSERT INTO statistic (id, created_at, updated_at, total_user_count, total_qrcode_event_count, total_guestbook_count, avg_qrcode_events_per_user, avg_guestbooks_per_qrcode_event)
            SELECT
                gen_random_uuid(),
                NOW(),
                NOW(),
                s.totalUsers,
                s.totalQrcodeEvent,
                s.totalGuestbooks,
                COALESCE(s.avgQrcodeEventsPerUser, 0),
                COALESCE(s.avgGuestbooksPerQrcodeEvent, 0)
            FROM (
                SELECT
                    COUNT(DISTINCT u.id) AS totalUsers,
                    COUNT(DISTINCT qe.id) AS totalQrcodeEvent,
                    COUNT(g.id) AS totalGuestbooks,
                    ((COUNT(DISTINCT qe.id) * 1.0) / NULLIF(COUNT(DISTINCT u.id), 0)) AS avgQrcodeEventsPerUser,
                    ((COUNT(g.id) * 1.0) / NULLIF(COUNT(DISTINCT qe.id), 0)) AS avgGuestbooksPerQrcodeEvent
                FROM
                    users u
                LEFT JOIN
                    qrcode_event qe ON u.id = qe.user_id
                LEFT JOIN
                    guestbook g ON qe.short_id = g.short_id
            ) AS s
            """, nativeQuery = true)
    void refreshStatistic();

    Optional<Statistic> findFirstByOrderByCreatedAtDesc();
}