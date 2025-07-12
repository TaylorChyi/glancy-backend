package com.glancy.backend.listener;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.glancy.backend.service.AlertService;

/**
 * Sends a startup notification email to all alert recipients when the
 * application is fully initialized.
 */
@Component
public class StartupNotificationListener {

    private final AlertService alertService;

    public StartupNotificationListener(AlertService alertService) {
        this.alertService = alertService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        alertService.sendAlert("服务启动", "Glancy 后端服务已启动");
    }
}
