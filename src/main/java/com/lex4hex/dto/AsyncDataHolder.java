package com.lex4hex.dto;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class AsyncDataHolder {
    @Getter
    private AtomicBoolean isRegistrationInProgress = new AtomicBoolean(false);
}
