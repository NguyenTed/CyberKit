package com.cyberkit.cyberkit_server.data;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import lombok.*;

@Entity
@Table(name = "admins")
@Getter
@Setter
public class AdminEntity extends AbstractUserEntity {}
