package com.cofinityx.waltid.waltidssikitdemo.dao;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.smartsensesolutions.java.commons.base.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HolderCredential implements BaseEntity {

    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "serial", nullable = false, unique = true)
    private Long id;

    @Column(nullable = false)
    private String alias;

    @Column(name = "group_name", nullable = false)
    private String group;

    @Column(nullable = false)
    private String data;

    @Column(nullable = false)
    private String credentialId;

    @Column(nullable = false)
    private String issuerId;

    @Column(nullable = false)
    private String holderTenant;
}
