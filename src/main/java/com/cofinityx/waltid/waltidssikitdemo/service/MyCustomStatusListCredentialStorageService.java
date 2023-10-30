package com.cofinityx.waltid.waltidssikitdemo.service;

import id.walt.credentials.w3c.VerifiableCredential;
import id.walt.services.did.DidService;
import id.walt.signatory.revocation.statuslist2021.StatusListCredentialStorageService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MyCustomStatusListCredentialStorageService extends StatusListCredentialStorageService {

    @Nullable
    @Override
    public VerifiableCredential fetch(@NotNull String id) {
        return super.fetch(id);
    }

    @Override
    public void store(@NotNull String id, @NotNull String purpose, @NotNull String bitString) {

        DidService.INSTANCE.
        super.store(id, purpose, bitString);
    }
}
