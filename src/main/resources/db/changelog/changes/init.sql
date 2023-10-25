--liquibase formatted sql

--changeset nitin:1
CREATE TABLE public.key_entity (
	id bigserial NOT NULL,
	key_id varchar(255) NOT NULL,
	key_alias varchar(255) NOT NULL,
	algorithm varchar(255) NOT NULL ,
	private_key text NOT NULL,
	public_key text NOT NULL,
	crypto_provider varchar(255) NOT NULL,
	CONSTRAINT uk_bpn UNIQUE (key_id),
	CONSTRAINT uk_did UNIQUE (key_alias),
	CONSTRAINT key_entity_pkey PRIMARY KEY (id)
);

CREATE TABLE public.wallet (
	id bigserial NOT NULL,
	tenant varchar(255) NOT NULL,
	name varchar(255) NOT NULL,
	did varchar(255) NOT NULL,
	did_document text NOT NULL ,
	CONSTRAINT wallet_uk_did UNIQUE (did),
	CONSTRAINT uk_tenant UNIQUE (tenant),
	CONSTRAINT wallet_pkey PRIMARY KEY (id)
);

CREATE TABLE public.holder_credential (
	id bigserial NOT NULL,
	alias varchar(255) NOT NULL,
	group_name varchar(255) NOT NULL,
	data text NOT NULL,
	credential_id varchar(255) NOT NULL,
	issuer_id varchar(255) NOT NULL,
	CONSTRAINT holder_credential_pkey PRIMARY KEY (id)
);