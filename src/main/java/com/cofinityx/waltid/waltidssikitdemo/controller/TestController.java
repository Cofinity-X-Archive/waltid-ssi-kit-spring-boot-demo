package com.cofinityx.waltid.waltidssikitdemo.controller;

import com.cofinityx.waltid.waltidssikitdemo.config.ApplicationSettings;
import com.cofinityx.waltid.waltidssikitdemo.dao.HolderCredential;
import com.cofinityx.waltid.waltidssikitdemo.dao.HolderCredentialRepository;
import com.cofinityx.waltid.waltidssikitdemo.dao.Wallet;
import com.cofinityx.waltid.waltidssikitdemo.dao.WalletRepository;
import com.cofinityx.waltid.waltidssikitdemo.dto.DidDocumentRequest;
import com.cofinityx.waltid.waltidssikitdemo.utils.Validate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import id.walt.auditor.Auditor;
import id.walt.auditor.VerificationResult;
import id.walt.auditor.policies.*;
import id.walt.credentials.w3c.*;
import id.walt.credentials.w3c.builder.W3CCredentialBuilder;
import id.walt.crypto.KeyAlgorithm;
import id.walt.crypto.KeyId;
import id.walt.custodian.Custodian;
import id.walt.model.Did;
import id.walt.model.DidMethod;
import id.walt.services.did.DidOptions;
import id.walt.services.did.DidService;
import id.walt.services.did.DidWebCreateOptions;
import id.walt.services.key.KeyService;
import id.walt.signatory.Ecosystem;
import id.walt.signatory.ProofConfig;
import id.walt.signatory.ProofType;
import id.walt.signatory.Signatory;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import kotlin.Unit;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@RestController
@Slf4j
public class TestController {

    private final KeyService keyService;

    private final ApplicationSettings applicationSettings;

    private final WalletRepository walletRepository;
    private final HolderCredentialRepository holderCredentialRepository;

    private final ObjectMapper  objectMapper;

    private final SimpleDateFormat simpleDateFormat;

    public TestController(KeyService keyService, ApplicationSettings applicationSettings, WalletRepository walletRepository,HolderCredentialRepository holderCredentialRepository, ObjectMapper objectMapper) {
        this.keyService = keyService;
        this.applicationSettings = applicationSettings;
        this.walletRepository = walletRepository;
        this.holderCredentialRepository = holderCredentialRepository;
        this.objectMapper = objectMapper;
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    @PostMapping(path = "/wallet", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createWallet(@RequestBody DidDocumentRequest didDocumentRequest){

        Wallet wallet = walletRepository.getByTenant(didDocumentRequest.getTenant());
        Validate.isNotNull(wallet).launch(new IllegalArgumentException("Wallet is already created"));

        KeyId keyId = keyService.generate(KeyAlgorithm.EdDSA_Ed25519);

        //create Did document
        DidOptions didOptions = new DidWebCreateOptions(applicationSettings.host(), didDocumentRequest.getTenant());
        String didString = DidService.INSTANCE.create(DidMethod.web, keyId.getId(), didOptions);
        log.info("Did document created -> {}", didString);
        id.walt.model.Did did = DidService.INSTANCE.load(didString);

        //save wallet
        walletRepository.save(Wallet.builder()
                .didDocument(did.encodePretty())
                .did(didString)
                .name(didDocumentRequest.getName())
                .tenant(didDocumentRequest.getTenant())
                .build());

        return ResponseEntity.ok(did.encodePretty());
    }


    @SneakyThrows
    @PostMapping(path = "/vc/membership")
    public ResponseEntity<String> issueVC(@RequestParam(name = "holderTenant") String tenant, @RequestParam(name = "asJwt", defaultValue = "false") boolean asJwt){

        //holder wallet
        Wallet holderWallet = walletRepository.getByTenant(tenant);
        Validate.isNull(holderWallet).launch(new IllegalStateException("Invalid tenant"));
        Did holderDid = Did.Companion.decode(holderWallet.getDidDocument());

        //issuer wallet
        Wallet issuerWallet = walletRepository.getByTenant(applicationSettings.baseTenant());
        Validate.isNull(issuerWallet).launch(new IllegalStateException("Issuer wallet not found"));
        Did issuedDid = Did.Companion.decode(issuerWallet.getDidDocument());

        // DidService.kt is using local cache, so we have to store did first, already asked in discord
        DidService.INSTANCE.storeDid(Objects.requireNonNull(issuedDid));
        DidService.INSTANCE.load(issuedDid.getId());


        //start building VC
        W3CCredentialBuilder w3CCredentialBuilder = new W3CCredentialBuilder();

        //set Expiry date
        var expiration = Instant.now().plus(30, ChronoUnit.DAYS);

        //set subject
        Map<String, ? extends Serializable> subject = Map.of("holderIdentifier", tenant,
                "startTime", simpleDateFormat.format(new Date()),
                "memberOf", "Catena-X",
                "id", holderDid.getId(),
                "type", "MembershipCredential",
                "status", "Active");

        w3CCredentialBuilder.buildSubject(subjectBuilder -> {
            subjectBuilder.setId(issuedDid.getId());
            try {
                subjectBuilder.setFromJson(objectMapper.writeValueAsString(subject));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            return Unit.INSTANCE;
        });

        //add context of membership VC
        W3CContext w3CContext = new W3CContext("https://cofinity-x.github.io/schema-registry/v1.1/businessPartnerData.json");
        w3CCredentialBuilder.addContext(w3CContext);

        //set type
        w3CCredentialBuilder.getType().add("MembershipCredential");  //this is not adding in type[]  ??

        //set issuer
        W3CIssuer w3CIssuer = new W3CIssuer(issuedDid.getId());

        String membershipVC = null;
        if(asJwt){
            membershipVC = Signatory.Companion.getService().issue(w3CCredentialBuilder, createProofConfig(issuedDid.getId(), holderDid.getId(), ProofType.JWT, expiration), w3CIssuer, false);
        }else{
            membershipVC = Signatory.Companion.getService().issue(w3CCredentialBuilder, createProofConfig(issuedDid.getId(), holderDid.getId(), ProofType.LD_PROOF, expiration), w3CIssuer, false);
        }

        //save VC in wallet
        VerifiableCredential verifiableCredential = VerifiableCredential.Companion.fromString(membershipVC);
        holderCredentialRepository.save(HolderCredential.builder()
                        .alias("aa")
                        .group("MembershipCredential")
                .credentialId(verifiableCredential.getId())
                .data(membershipVC)
                .issuerId(issuedDid.getId())
                .build());

        return ResponseEntity.ok(membershipVC);
    }


    @PostMapping(path = "/vc/verify", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VerificationResult> verifyVC(@RequestBody String vc) throws JsonProcessingException {
        VerifiableCredential verifiableCredential = VerifiableCredential.Companion.fromString(vc);
        //set schema URL, this is not working as they are following different schema type: https://raw.githubusercontent.com/walt-id/waltid-ssikit-vclib/master/src/test/resources/schemas/VerifiableId.json
        JsonSchemaPolicyArg jsonSchemaPolicyArg = new JsonSchemaPolicyArg("https://cofinity-x.github.io/schema-registry/v1.1/businessPartnerData.json");
        //new JsonSchemaPolicy(jsonSchemaPolicyArg) This is not working
        VerificationResult verify = Auditor.Companion.getService().verify(verifiableCredential, List.of(new SignaturePolicy(), new ExpirationDateAfterPolicy(), new CredentialStatusPolicy()));
        return ResponseEntity.ok(verify);
    }


    @GetMapping(path ="/{tenant}/did.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<id.walt.model.Did> getDidResolve(@Parameter(description = "tenant",examples = {@ExampleObject(name = "tenant", value = "smartSense", description = "tenant")}) @PathVariable(name = "tenant") String tenant) {
        Wallet holderWallet = walletRepository.getByTenant(tenant);
        Validate.isNull(holderWallet).launch(new IllegalStateException("Invalid tenant"));
        Did holderDid = Did.Companion.decode(holderWallet.getDidDocument());
        return ResponseEntity.status(HttpStatus.OK).body(holderDid);
    }
    @PostMapping(path = "/vp", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createVP(@RequestBody String vc,
                                           @RequestParam(name = "tenant") String tenant,
                                           @RequestParam(name = "asJwt", defaultValue = "false") boolean asJwt){
        Wallet holderWallet = walletRepository.getByTenant(tenant);
        Validate.isNull(holderWallet).launch(new IllegalArgumentException("Wallet not found"));

        VerifiableCredential verifiableCredential = VerifiableCredential.Companion.fromString(vc);

        String presentation = null;
        if(asJwt){
            var expiration = Instant.now().plus(30, ChronoUnit.MINUTES);
            presentation = Custodian.Companion.getService().createPresentation(List.of(new PresentableCredential(verifiableCredential, null, false)), holderWallet.getDid(), null, null, null, expiration);
        }else{
            presentation = Custodian.Companion.getService().createPresentation(List.of(new PresentableCredential(verifiableCredential, null, false)), holderWallet.getDid(), null, null, null, null);

        }
        return ResponseEntity.ok(presentation);
    }

    @GetMapping(path ="/vc", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<HolderCredential>> getVC() {
        return ResponseEntity.status(HttpStatus.OK).body(holderCredentialRepository.findAll());
    }

    @EventListener(ApplicationReadyEvent.class)
    public void createBaseTenantWallet(){
        Wallet baseWallet = walletRepository.getByTenant(applicationSettings.baseTenant());
        if(baseWallet == null){
            DidDocumentRequest didDocumentRequest = new DidDocumentRequest();
            didDocumentRequest.setTenant(applicationSettings.baseTenant());
            didDocumentRequest.setName(applicationSettings.baseTenant());
            createWallet(didDocumentRequest);
            log.info("Wallet for base tenant is created");
        }else{
            log.info("Wallet for base tenant is present");
        }
    }




    private ProofConfig createProofConfig(String issuerDid, String subjectDid, ProofType proofType, Instant expiration) {
        return new ProofConfig(issuerDid = issuerDid, subjectDid = subjectDid, null, null, proofType, null, null,
                null, null, null, null, expiration, null, null, null, Ecosystem.DEFAULT,
                null, "revocation", "", null);
    }
}
