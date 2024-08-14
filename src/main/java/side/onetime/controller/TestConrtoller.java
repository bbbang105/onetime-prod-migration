package side.onetime.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestConrtoller {

    @GetMapping("/api/v1/test")
    public String test() {
        return "Onetime!";
    }
}