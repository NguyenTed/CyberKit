package com.cyberkit.cyberkit_server.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {
    @Bean
    public ObjectMapper myObjectMapper(){
        ObjectMapper objectMapper= new ObjectMapper();
        return objectMapper;
    }
    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }
}
