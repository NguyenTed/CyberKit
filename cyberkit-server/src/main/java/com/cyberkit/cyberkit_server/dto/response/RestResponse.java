package com.cyberkit.cyberkit_server.dto.response;

import lombok.*;
import com.fasterxml.jackson.annotation.JsonInclude;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestResponse<K> {
    @Builder.Default
    private int statusCode = 200;
    private String error;
    // message is string or list.
    private Object message;
    private K data;
}

