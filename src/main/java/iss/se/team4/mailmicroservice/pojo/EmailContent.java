package iss.se.team4.mailmicroservice.pojo;


import lombok.Data;

@Data
public class EmailContent {
    String emailAddress;
    String userName;
    String productInfo;
    String updatedItem;
    String itemInfo;
}
