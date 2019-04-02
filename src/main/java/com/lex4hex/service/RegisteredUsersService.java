package com.lex4hex.service;

import com.lex4hex.dto.AsyncDataHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashSet;
import java.util.List;

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
        HashSet<Integer> numbersSet = new HashSet<>();

        while (asyncDataHolder.getIsRegistrationInProgress().get()) {
            List<Integer> numbers =
                    restTemplate.exchange(registrationServiceAddress + "/getNumbers", HttpMethod.GET, null,
                            new ParameterizedTypeReference<List<Integer>>() {
                            }).getBody();

            if (numbers == null || numbers.isEmpty()) {
                continue;
            }

            numbers.removeAll(numbersSet);

            log.info("Registered: " + numbers);

            for (Integer number : numbers) {
                try {
                    restTemplate.getForObject(lbNodeUrl + "/add/" + number, String.class);
                } catch (Exception e) {
                    //ignored
                }
            }

            numbersSet.addAll(numbers);

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
