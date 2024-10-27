package iss.se.team4.mailmicroservice.controller;

import iss.se.team4.mailmicroservice.dto.WantedEmailDto;
import iss.se.team4.mailmicroservice.exception.SendMailException;
import iss.se.team4.mailmicroservice.pojo.EmailContent;
import iss.se.team4.mailmicroservice.pojo.Result;
import iss.se.team4.mailmicroservice.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mail")
public class MailController {
    @Autowired
    private MailService mailService;

    @PostMapping("/welcome")
    public Result sendWelcomeMail(@RequestBody EmailContent emailContent) throws SendMailException {
        mailService.sendWelcomeEmail(emailContent.getEmailAddress(), emailContent.getUserName());
        return Result.success();
    }

    @PostMapping("/wanted")
    public Result sendWantedMail(@RequestBody WantedEmailDto wantedEmailDto) throws SendMailException {
        mailService.sendWantedEmail(wantedEmailDto.getBuyerEmail(), wantedEmailDto.getBuyerName(), wantedEmailDto.getSellerEmail(),
                wantedEmailDto.getSellerName(), wantedEmailDto.getProductName());
        return Result.success();
    }

    @PostMapping("/customized")
    public Result sendCustomizedEmail(@RequestBody EmailContent emailContent) throws SendMailException {
        mailService.sendCustomizedEmail(emailContent.getEmailAddress(), emailContent.getUserName(), emailContent.getUpdatedItem(), emailContent.getItemInfo());
        return Result.success();
    }
}
