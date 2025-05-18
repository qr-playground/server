package com.example.demo.domain.guestbook.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.domain.guestbook.entity.Guestbook;
import com.example.demo.domain.qrcode.entity.QrcodeEvent;

public interface GuestbookRepository extends JpaRepository<Guestbook, UUID> {

    Page<Guestbook> findAllByQrcodeEvent(QrcodeEvent qrcodeEvent, Pageable pageable);

}
