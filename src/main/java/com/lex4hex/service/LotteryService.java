package com.lex4hex.service;

import com.lex4hex.dto.AsyncDataHolder;
import com.lex4hex.dto.NodeNumber;
import com.lex4hex.dto.NodeStatusDto;
import com.lex4hex.dto.NumbersDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class LotteryService {
    private RestTemplate restTemplate;

    @Value("${first.node.address}")
    private String firstNodeUrl;

    @Value("${second.node.address}")
    private String secondNodeUrl;

    @Value("${third.node.address}")
    private String thirdNodeUrl;

    @Value("${registration.service.address}")
    private String registrationServiceAddress;

    @Value("${lb.node.address}")
    private String lbNodeUrl;

    private AsyncDataHolder asyncDataHolder;

    private RegisteredUsersService registeredUsersService;

    @Autowired
    public LotteryService(RestTemplate restTemplate, AsyncDataHolder asyncDataHolder,
            RegisteredUsersService registeredUsersService) {
        this.restTemplate = restTemplate;
        this.asyncDataHolder = asyncDataHolder;
        this.registeredUsersService = registeredUsersService;
    }

    public NumbersDto getNodeData(NodeNumber nodeNumber) {
        String url = getNodeUrl(nodeNumber);

        return restTemplate.exchange(url + "/all", HttpMethod.GET, null, NumbersDto.class).getBody();
    }

    public void openRegistration() {
        if (asyncDataHolder.getIsRegistrationInProgress().get()) {
            throw new RuntimeException("Registration is already in progress");
        }

        asyncDataHolder.getIsRegistrationInProgress().set(true);
        restTemplate.getForObject(registrationServiceAddress + "/startLottery", Void.class);
        registeredUsersService.getAllRegisteredUsers();
    }

    public void closeRegistration() {
        asyncDataHolder.getIsRegistrationInProgress().set(false);
        restTemplate.getForObject(registrationServiceAddress + "/reset", Void.class);
        restTemplate.getForObject(registrationServiceAddress + "/stopLottery", Void.class);
    }

    public NodeStatusDto getNodeStatus(NodeNumber nodeNumber) {
        String url = getNodeUrl(nodeNumber);

        return restTemplate.exchange(url + "/status", HttpMethod.GET, null, NodeStatusDto.class).getBody();
    }

    private String getNodeUrl(NodeNumber nodeNumber) {
        String url = null;

        switch (nodeNumber) {
            case FIRST:
                url = firstNodeUrl;
                break;
            case SECOND:
                url = secondNodeUrl;
                break;
            case THIRD:
                url = thirdNodeUrl;
                break;
        }

        return url;
    }

    public NumbersDto getAllClusterNumbers() {
        return restTemplate.exchange(lbNodeUrl + "/all", HttpMethod.GET, null, NumbersDto.class).getBody();
    }

    public Boolean isNodeAlive(NodeNumber number) {
        String nodeUrl = getNodeUrl(number);

        ResponseEntity<NodeStatusDto> exchange;

        try {
            exchange = restTemplate.exchange(nodeUrl + "/status", HttpMethod.GET, null, NodeStatusDto.class);
        } catch (Exception e) {
            return false;
        }

        return exchange != null && exchange.getStatusCode() == HttpStatus.OK;

    }
}
