package com.lex4hex.service;

import com.lex4hex.dto.AsyncDataHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Service
@Slf4j
public class RegisteredUsersService {

    private AsyncDataHolder asyncDataHolder;

    private RestTemplate restTemplate;

    @Value("${registration.service.address}")
    private String registrationServiceAddress;

    @Value("${lb.node.address}")
    private String lbNodeUrl;

    @Autowired
    public RegisteredUsersService(AsyncDataHolder asyncDataHolder,
            RestTemplate restTemplate) {
        this.asyncDataHolder = asyncDataHolder;
        this.restTemplate = restTemplate;
    }

    @Async
    public void getAllRegisteredUsers() {
        while (asyncDataHolder.getIsRegistrationInProgress().get()) {
            Integer[] numbers =
                    restTemplate.getForObject(registrationServiceAddress + "/getNumbers", Integer[].class);

            if (numbers == null) {
                continue;
            }

            log.info("Registered: " + Arrays.toString(numbers));

            for (Integer number : numbers) {
                restTemplate.getForObject(lbNodeUrl + "/add/" + number, String.class);
            }

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
