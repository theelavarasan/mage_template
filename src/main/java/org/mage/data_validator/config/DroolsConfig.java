package org.mage.data_validator.config;
import jakarta.annotation.PostConstruct;
import org.kie.api.KieServices;
import org.kie.api.builder.*;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;
import org.mage.data_validator.rules.ValidationHeaderRule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Configuration
public class DroolsConfig {

    @Value("${expected.headers}")
    private String expectedHeadersString;

    private List<String> expectedHeaders;

    @PostConstruct
    public void init() {
        this.expectedHeaders = Arrays.asList(StringUtils.commaDelimitedListToStringArray(expectedHeadersString));
    }

//    @Bean
//    public KieContainer kieContainer() {
//        return KieServices.Factory.get().getKieClasspathContainer();
//    }
//
//    @Bean
//    public KieSession kieSession() {
//        KieSession kieSession = kieContainer().newKieSession("rulesKSession");
////        kieSession.setGlobal("errorMessage", "");
//        return kieSession;
//    }

    private KieServices kieServices = KieServices.Factory.get();

    private KieFileSystem getKieFileSystem() throws IOException {
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
        kieFileSystem.write(ResourceFactory.newClassPathResource("rules/validation_rules.drl"));
        return kieFileSystem;

    }

    @Bean
    public KieContainer getKieContainer() throws IOException {
        System.out.println("Container created...");
        getKieRepository();
        KieBuilder kb = kieServices.newKieBuilder(getKieFileSystem());
        kb.buildAll();
        KieModule kieModule = kb.getKieModule();
        KieContainer kContainer = kieServices.newKieContainer(kieModule.getReleaseId());
        return kContainer;

    }

    private void getKieRepository() {
        final KieRepository kieRepository = kieServices.getRepository();
        kieRepository.addKieModule(new KieModule() {
            public ReleaseId getReleaseId() {
                return kieRepository.getDefaultReleaseId();
            }
        });
    }

    @Bean
    public KieSession kieSession() throws IOException {
        System.out.println("session created...");
        KieSession kieSession = getKieContainer().newKieSession();
        return kieSession;

    }

//    @Bean
//    public ValidationHeaderRule validateHeaderRule() {
//        return new ValidationHeaderRule(expectedHeaders);
//    }
}
