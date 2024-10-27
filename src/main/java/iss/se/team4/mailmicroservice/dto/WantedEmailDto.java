package iss.se.team4.mailmicroservice.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class WantedEmailDto {
    private String buyerEmail;
    private String buyerName;
    private String sellerEmail;
    private String sellerName;
    private String productName;
}
