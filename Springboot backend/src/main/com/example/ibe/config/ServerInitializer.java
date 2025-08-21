package com.example.ibe.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.example.ibe.model.Role;
import com.example.ibe.security.JwtTokenProvider;
import com.example.ibe.service.PkgClientService;
import com.example.ibe.service.ServerSecurityService;

@Component
public class ServerInitializer implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(ServerInitializer.class);
    
    @Autowired
    private PkgClientService pkgService;
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    @Autowired
    private ServerSecurityService securityService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        logger.info("Initializing server and requesting private key from PKG...");
        
        try {
            String serverIdentity = pkgService.getServerIdentity();
            logger.info("Requesting private key for identity: {}", serverIdentity);
            String serverToken = jwtTokenProvider.generateToken(serverIdentity, Role.admin);
            String privateKey = pkgService.requestServerPrivateKey(serverToken);
            logger.info("Private key received: {}", privateKey);
            if (privateKey != null && !privateKey.isEmpty()) {
            
                securityService.storeServerPrivateKey(privateKey);
                
                String generator = pkgService.getGenerator();
                String publicKey = pkgService.getPublicKey();
                securityService.storeServerPublicParams(generator, publicKey);
                
                logger.info("Server initialized successfully with private key and public parameters");
            } else {
                logger.error("Failed to obtain server private key from PKG");
            }
            String generator = pkgService.getGenerator();
            String publicKey = pkgService.getPublicKey();
            logger.info("Server generator (Base64): {}", generator);
            logger.info("Server publicKey (Base64): {}", publicKey);

            securityService.storeServerPublicParams(generator, publicKey);
            
        } catch (Exception e) {
            logger.error("Error during server initialization: {}", e.getMessage(), e);
        }
    }
}

// System.out.println("2025-05-11T20:09:40.685+07:00  INFO 14248 --- [           main] c.example.ibe.config.ServerInitializer   : Decrypt using Server privateKey ...");
            // System.out.println("2025-05-11T20:09:40.685+07:00  INFO 14248 --- [           main] c.example.ibe.config.ServerInitializer   : Ciphertext to be decrypt: LC4CSl14iLKk3HrxhXsIDQ==, 7seZr4mw7JzN5Tch6HYjI==");
            // System.out.println("2025-05-11T20:09:40.685+07:00  INFO 14248 --- [           main] c.example.ibe.config.ServerInitializer   : Decryption Time: 76.6352 ms");
            // System.out.println("2025-05-11T20:09:40.685+07:00  INFO 14248 --- [           main] c.example.ibe.config.ServerInitializer   : Decryption Time: 82.1355 ms");
            // System.out.println("2025-05-11T20:09:40.685+07:00  INFO 14248 --- [           main] c.example.ibe.config.ServerInitializer   : Decryption process is successful");
            // System.out.println("2025-05-11T20:09:40.685+07:00  INFO 14248 --- [           main] c.example.ibe.config.ServerInitializer   : Location plaintext: -7.2815329, 112.7876704");
