package com.cyberkit.cyberkit_server.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SecondaryRow;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RestResponse<K> {
    private int statusCode;
    private String error;
    // message is string or list.
    private Object message;
    private K data;

}

