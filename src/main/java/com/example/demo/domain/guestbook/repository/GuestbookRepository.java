package com.example.demo.domain.guestbook.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.domain.guestbook.entity.Guestbook;
import com.example.demo.domain.qrcode.entity.QrcodeEvent;

public interface GuestbookRepository extends JpaRepository<Guestbook, UUID> {

     Page<Guestbook> findAllByQrcodeEvent(QrcodeEvent qrcodeEvent, Pageable pageable);

     @Query("""
                   select g from Guestbook g
                    where g.qrcodeEvent = :qrcodeEvent
                      and (
                           g.createdAt < :beforeCreatedAt
                           or (g.createdAt = :beforeCreatedAt and g.id < :beforeId)
                      )
                    order by g.createdAt desc, g.id desc
               """)
     List<Guestbook> findOlderThan(
               @Param("qrcodeEvent") QrcodeEvent qrcodeEvent,
               @Param("beforeCreatedAt") LocalDateTime beforeCreatedAt,
               @Param("beforeId") UUID beforeId,
               Pageable pageable);

     @Query("""
               select g from Guestbook g
                    where g.qrcodeEvent = :qrcodeEvent
                    and ( g.createdAt > :afterCreatedAt
                         or (g.createdAt = :afterCreatedAt and g.id > :afterId) )
                    order by g.createdAt asc, g.id asc
               """)
     List<Guestbook> findAfterCursor(
               @Param("qrcodeEvent") QrcodeEvent qrcodeEvent,
               @Param("afterCreatedAt") LocalDateTime afterCreatedAt,
               @Param("afterId") UUID afterId,
               Pageable pageable);
}
