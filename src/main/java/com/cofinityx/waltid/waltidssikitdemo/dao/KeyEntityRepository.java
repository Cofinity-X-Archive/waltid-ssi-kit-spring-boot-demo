package com.cofinityx.waltid.waltidssikitdemo.dao;

import com.smartsensesolutions.java.commons.base.repository.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KeyEntityRepository extends BaseRepository<KeyEntity, Long>{


    KeyEntity getByKeyId(String keyId);

    KeyEntity getByKeyAlias(String alias);

    KeyEntity getByKeyAliasOrKeyId(String alias, String keyId);
}
