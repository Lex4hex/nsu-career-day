package com.lex4hex.controller;

import com.lex4hex.dto.NodeNumber;
import com.lex4hex.dto.NodeStatusDto;
import com.lex4hex.dto.NumbersDto;
import com.lex4hex.service.LotteryService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/")
@AllArgsConstructor
public class LotteryController {
    private LotteryService lotteryService;

    @GetMapping("/node/{number}/data")
    public NumbersDto getNodeData(@PathVariable NodeNumber number) {
        return lotteryService.getNodeData(number);
    }

    @GetMapping("/node/{number}/status")
    public NodeStatusDto getNodeStatus(@PathVariable NodeNumber number) {
        return lotteryService.getNodeStatus(number);
    }

    @GetMapping("/node/{number}/isAlive")
    public Boolean isNodeAlive(@PathVariable NodeNumber number) {
        return lotteryService.isNodeAlive(number);
    }

    @GetMapping("/allClusterNumbers")
    public NumbersDto getAllNumbers() {
        return lotteryService.getAllClusterNumbers();
    }

    @PostMapping("/openRegistration")
    public void openRegistration() {
        lotteryService.openRegistration();
    }

    @PostMapping("/closeRegistration")
    public void closeRegistration() {
        lotteryService.closeRegistration();
    }
}
