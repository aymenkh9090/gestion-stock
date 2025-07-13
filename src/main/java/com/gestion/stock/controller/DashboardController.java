package com.gestion.stock.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    @GetMapping("/superadmin")
    public String superAdmin() {
        return "dashboard/superadmin/dashboard_superadmin";
       // return "index";
    }

    @GetMapping("/user")
    public String user(){
        return "dashboard/user/dashboard_user";
    }

    @GetMapping("/admin")
    public String admin(){
        return "dashboard/admin/dashboard_admin";
    }



}
