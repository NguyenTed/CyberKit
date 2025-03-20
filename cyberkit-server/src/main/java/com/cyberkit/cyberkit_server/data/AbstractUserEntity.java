package com.cyberkit.cyberkit_server.data;

import com.cyberkit.cyberkit_server.enums.GenderEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Inheritance(strategy = InheritanceType.JOINED) // or SINGLE_TABLE if preferred
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractUserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Date dateOfBirth;
    @Enumerated(EnumType.STRING)
    private GenderEnum gender;

}
