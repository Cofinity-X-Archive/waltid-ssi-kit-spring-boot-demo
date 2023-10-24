package com.cofinityx.waltid.waltidssikitdemo.service;

import com.cofinityx.waltid.waltidssikitdemo.dao.KeyEntity;
import com.cofinityx.waltid.waltidssikitdemo.dao.KeyEntityRepository;
import com.cofinityx.waltid.waltidssikitdemo.utils.StaticContextAccessor;
import id.walt.crypto.*;
import id.walt.services.keystore.KeyStoreService;
import id.walt.services.keystore.KeyType;
import kotlin.NotImplementedError;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Slf4j
public class MyCustomKeyStoreService extends KeyStoreService{


    @Override
    public void addAlias(@NotNull KeyId keyId, @NotNull String alias) {
        String id = keyId.getId();
        KeyEntity keyEntity = getRepo().getByKeyId(id);
        keyEntity.setKeyAlias(alias);
        getRepo().save(keyEntity);
        log.info("key alias added for keyId ->{} , alias->{}", id, alias);
    }

    @Override
    public void delete(@NotNull String alias) {
        throw new NotImplementedError();
    }

    @Nullable
    @Override
    public String getKeyId(@NotNull String alias) {
        KeyEntity keyEntity = getRepo().getByKeyAlias(alias);
        return keyEntity.getKeyId();
    }

    @NotNull
    @Override
    public List<Key> listKeys() {
        return new ArrayList<>();
    }

    @SneakyThrows
    @NotNull
    @Override
    public Key load(@NotNull String alias, @NotNull KeyType keyType) {
        KeyEntity keyEntity = getRepo().getByKeyAliasOrKeyId(alias, alias);
        KeyId keyId = new KeyId(keyEntity.getKeyId());
        return CryptFunKt.buildKey(keyId.getId(), keyEntity.getAlgorithm(), keyEntity.getCryptoProvider(), keyEntity.getPublicKey(), keyEntity.getPrivateKey(), KeyFormat.PEM);
    }

    @Override
    public void store(@NotNull Key key) {
        log.info("Storing key with id ->{}", key.getKeyId());
        KeyEntity keyEntity = KeyEntity.builder()
                .keyId(key.getKeyId().getId())
                .algorithm(key.getAlgorithm().name())
                .keyAlias(UUID.randomUUID().toString())
                .privateKey(Base64.getEncoder().encodeToString(key.getKeyPair().getPrivate().getEncoded()))
                .publicKey(Base64.getEncoder().encodeToString(key.getKeyPair().getPublic().getEncoded()))
                .cryptoProvider(key.getCryptoProvider().name())
                .build();
        KeyEntity save = getRepo().save(keyEntity);
        log.info("Key stored in database with id ->"+save.getId());

    }

    private static KeyEntityRepository getRepo(){
        return StaticContextAccessor.getBean(KeyEntityRepository.class);
    }
}
