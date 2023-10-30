## [Walt.id SSI kit](https://github.com/walt-id/waltid-ssikit) demo with spring boot and database storage


## Run in local

1. Run PostgresSQL server using local setup or docker
2. Create Database
3. Set database configuration in application.yaml file 
4. Set correct path for signatory.conf in service-matrix.properties file


### Current issue/limitation

1. Schema validation is not working as expected. WaltId following different schema json
2. No storage for did document. No storage service available
3. Credential subject does not support array
4. Can not add Type in list
5. Not sure about signatory.conf file
6. Web did resolution not happening over the internet
7. Revocation supports only did:key method and did storage not support SQL(Same as point 1). Can we us same did:web for revocation?