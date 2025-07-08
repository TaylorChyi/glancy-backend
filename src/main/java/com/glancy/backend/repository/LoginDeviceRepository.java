package com.glancy.backend.repository;

import com.glancy.backend.entity.LoginDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoginDeviceRepository extends JpaRepository<LoginDevice, Long> {
}
