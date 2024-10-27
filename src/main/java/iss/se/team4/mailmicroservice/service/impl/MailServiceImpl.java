package iss.se.team4.mailmicroservice.service.impl;

import iss.se.team4.mailmicroservice.exception.SendMailException;
import iss.se.team4.mailmicroservice.service.MailService;
import org.junit.Assert;
import iss.se.team4.mailmicroservice.utils.MailUtils;
import org.springframework.stereotype.Service;

import static iss.se.team4.mailmicroservice.utils.MailUtils.SMTP_QQ;

@Service
public class MailServiceImpl implements MailService {

    static {
        initialize();
    }

    private static void initialize() {
        MailUtils.config(SMTP_QQ(false), "1602531630@qq.com", "psfqluaincyxbahd");
    }

    @Override
    public void sendWelcomeEmail(String emailAddr, String username) throws SendMailException {
        MailUtils.subject("[SecondHand] Welcome to SecondHand")
                .from("SecondHand Team")
                .to(emailAddr)
                .text("Hi " + username + "! You have successfully created your account in SecondHand. \n\n" +
                        "Join our community of savvy shoppers and sellers, and turn your unused items into cash. \n\nHappy trading!")
                .send();
        Assert.assertTrue(true);
    }

    @Override
    public void sendWantedEmail(String buyerEmail, String buyerName, String sellerEmail, String sellerName, String productInfo) throws SendMailException {
        MailUtils.subject("[SecondHand] Someone Likes Your Item")
                .from("SecondHand Team")
                .to(sellerEmail)
                .text("Hi " + sellerName + "! Someone is interested in your product: " + productInfo + ". Come and see details. See the buyer Info below:" +
                        "\n\nbuyerName: "+ buyerName +
                        "\n\nbuyerEmail: "+ buyerEmail +
                        "\n\nHappy trading!")
                .send();
        Assert.assertTrue(true);
    }

    @Override
    public void sendCustomizedEmail(String emailAddr, String username, String updatedItem, String itemInfo) throws SendMailException {
        MailUtils.subject("[SecondHand] Successfully Update Your Info")
                .from("SecondHand Team")
                .to(emailAddr)
                .text("Hi " + username + "! You have successfully updated your " + updatedItem + " to " + itemInfo + ". \n\nHappy trading!")
                .send();
        Assert.assertTrue(true);
    }
}
