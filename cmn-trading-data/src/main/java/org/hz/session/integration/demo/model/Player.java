package org.hz.session.integration.demo.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.Table;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table
public class Player extends BasicId {

    private String username;
    private String password;

}
