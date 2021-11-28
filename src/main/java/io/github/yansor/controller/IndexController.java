package io.github.yansor.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@RestController
public class IndexController {

    @GetMapping(value = "test")
    public String index() throws ParseException {

        Date date = new Date();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date1 = sdf.format(date);
        Date date2 = new SimpleDateFormat("yyyy-MM-dd").parse(date1);
        Calendar calendar = Calendar.getInstance();
        Date date3 = calendar.getTime();
        long date4 = date3.getTime();
        long date5 = date4 / 1000;


        System.out.println("date: " + date);
        System.out.println("date1: " + date1);
        System.out.println("date2: " + date2);
        System.out.println("date3: " + date3);
        System.out.println("date4: " + date4);
        System.out.println("date5: " + date5);


        return "";
    }




}
