package com.cofinityx.waltid.waltidssikitdemo.dao;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.smartsensesolutions.java.commons.base.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.security.KeyPair;
import java.util.List;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KeyEntity  implements BaseEntity {

    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "serial", nullable = false, unique = true)
    private Long id;

    @Column(nullable = false)
    private String keyId;

    @Column(nullable = false, unique = true)
    private String keyAlias;

    @Column(nullable = false)
    private String algorithm;

    @Column(nullable = false)
    private String privateKey;

    @Column(nullable = false)
    private String publicKey;

    @Column(nullable = false)
    private String cryptoProvider;

}