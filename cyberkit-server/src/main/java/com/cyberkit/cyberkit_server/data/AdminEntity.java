package com.cyberkit.cyberkit_server.data;

import com.cyberkit.cyberkit_server.enums.GenderEnum;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "admins")
@Getter
@Setter
public class AdminEntity extends AbstractUserEntity{

}
