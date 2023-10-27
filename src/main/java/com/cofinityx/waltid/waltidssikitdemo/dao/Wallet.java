package com.cofinityx.waltid.waltidssikitdemo.dao;

import com.cofinityx.waltid.waltidssikitdemo.utils.StringToDidDocumentConverter;
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
public class Wallet implements BaseEntity {

    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "serial", nullable = false, unique = true)
    private Long id;

    @Column(nullable = false, unique = true)
    private String tenant;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String did;

    @Column(nullable = false)
    @Convert(converter = StringToDidDocumentConverter.class)
    private String didDocument;

}