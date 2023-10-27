package com.cofinityx.waltid.waltidssikitdemo.dao;

import com.smartsensesolutions.java.commons.base.repository.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HolderCredentialRepository extends BaseRepository<HolderCredential, Long> {
    List<HolderCredential> getByHolderTenant(String tenant);
}
