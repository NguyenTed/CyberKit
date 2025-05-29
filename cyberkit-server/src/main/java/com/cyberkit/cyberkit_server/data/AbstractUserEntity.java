package com.cyberkit.cyberkit_server.data;

import java.util.Date;

import jakarta.persistence.*;

import com.cyberkit.cyberkit_server.enums.GenderEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Inheritance(strategy = InheritanceType.JOINED) // or SINGLE_TABLE if preferred
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "abstract_users")
public abstract class AbstractUserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Date dateOfBirth;

    @Enumerated(EnumType.STRING)
    private GenderEnum gender;
}
