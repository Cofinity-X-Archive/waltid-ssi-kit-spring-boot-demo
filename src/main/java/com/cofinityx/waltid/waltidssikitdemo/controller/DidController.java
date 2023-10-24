package com.cofinityx.waltid.waltidssikitdemo.controller;

import com.cofinityx.waltid.waltidssikitdemo.dto.DidDocumentRequest;
import id.walt.crypto.KeyAlgorithm;
import id.walt.crypto.KeyId;
import id.walt.model.DidMethod;
import id.walt.services.did.DidOptions;
import id.walt.services.did.DidService;
import id.walt.services.did.DidWebCreateOptions;
import id.walt.services.key.KeyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class DidController {

    private final KeyService keyService;

    public DidController(KeyService keyService) {
        this.keyService = keyService;
    }

    @PostMapping(path = "/did", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createDid(@RequestBody DidDocumentRequest didDocumentRequest){
        // generate an asymmetric key of type EdDSA ED25519
        KeyId keyId = keyService.generate(KeyAlgorithm.EdDSA_Ed25519);
        //create Did document
        String domain = "localhost";
        DidOptions didOptions = new DidWebCreateOptions(domain, didDocumentRequest.getTenant());
        String didString = DidService.INSTANCE.create(DidMethod.web, keyId.getId(), didOptions);
        id.walt.model.Did did = DidService.INSTANCE.load(didString);
        return ResponseEntity.ok(did.encodePretty());
    }
}
