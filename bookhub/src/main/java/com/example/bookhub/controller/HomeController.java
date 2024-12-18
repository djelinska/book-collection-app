package com.example.bookhub.controller;

import com.example.bookhub.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {
    public final StatisticsService statisticsService;

    @GetMapping("/")
    public String welcome() {
        return "welcome";
    }

    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("stats", statisticsService.getCurrentUserStats());
        return "home";
    }
}
