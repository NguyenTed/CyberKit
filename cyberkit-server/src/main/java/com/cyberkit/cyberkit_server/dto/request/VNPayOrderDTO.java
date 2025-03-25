package com.cyberkit.cyberkit_server.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VNPayOrderDTO {
    private Long subscriptionId;
    private String transactionStatus;
    private Date payDate;
    private String transactionNo;
}
