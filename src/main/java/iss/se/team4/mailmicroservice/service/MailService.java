package iss.se.team4.mailmicroservice.service;

import iss.se.team4.mailmicroservice.exception.SendMailException;

public interface MailService {
    // sending a welcome email when user register new account
    void sendWelcomeEmail(String emailAddr, String username) throws SendMailException;

    // send a notice email to seller when a buyer "wanted" his/her product
    void sendWantedEmail(String buyerEmail, String buyerName, String sellerEmail, String sellerName, String productInfo) throws SendMailException;

    // send a customize email
    void sendCustomizedEmail(String emailAddr, String username, String updatedItem, String itemInfo) throws SendMailException;

}
