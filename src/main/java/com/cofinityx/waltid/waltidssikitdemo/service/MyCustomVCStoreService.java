package com.cofinityx.waltid.waltidssikitdemo.service;

import id.walt.credentials.w3c.VerifiableCredential;
import id.walt.services.vcstore.VcStoreService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Slf4j
public class MyCustomVCStoreService extends VcStoreService {

    @Override
    public boolean deleteCredential(@NotNull String alias, @NotNull String group) {
        return super.deleteCredential(alias, group);
    }

    @Nullable
    @Override
    public VerifiableCredential getCredential(@NotNull String id, @NotNull String group) {
        return super.getCredential(id, group);
    }

    @NotNull
    @Override
    public List<String> listCredentialIds(@NotNull String group) {
        return super.listCredentialIds(group);
    }

    @NotNull
    @Override
    public List<VerifiableCredential> listCredentials(@NotNull String group) {
        return super.listCredentials(group);
    }

    @Override
    public void storeCredential(@NotNull String alias, @NotNull VerifiableCredential vc, @NotNull String group) {
        super.storeCredential(alias, vc, group);
    }
}
