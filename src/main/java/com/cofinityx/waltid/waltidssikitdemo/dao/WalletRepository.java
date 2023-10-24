package com.cofinityx.waltid.waltidssikitdemo.dao;

import com.smartsensesolutions.java.commons.base.repository.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletRepository extends BaseRepository<Wallet, Long>{


    KeyEntity getByDid(String did);
}
