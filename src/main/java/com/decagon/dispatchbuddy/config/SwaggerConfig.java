package com.decagon.dispatchbuddy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebFlux;

@Configuration
@EnableSwagger2
public class SwaggerConfig {                                    
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.OAS_30)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();
    }
    
     private ApiInfo metaData() {
        return new ApiInfoBuilder()
                 .title("Lenos Shopping App")
                .description("The APIs have been built by Rabiu Aliyu, CEO/founder Lenos Nigeria.")
                .version("0.0.1")
                .license("Apache License Version 1.0")
                .licenseUrl("https://www.apache.org/licenses/LICENSE-2.0\"")
                .contact(new Contact("Rabiu Aliyu", "https://www.linkedin.com/in/rabiyu-aliyu-461569157", "net.rabiualiyu@gmail.com"))
                .build();
    }

}