package com.abs.utility.config;


import com.abs.utility.api.upload.vo.ServiceProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@EnableSwagger2
@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {

    private final ServiceProperties serviceProperties;

    @Value("${spring.profiles.active}")
    private String profiles;


    // http://localhost/swagger-ui/

    @Bean
    public Docket api() {

        String host = "";
        if ("dev".equals(profiles) || "prod".equals(profiles)) {
            host = serviceProperties.getDomain();
        }
        Docket result = new Docket(DocumentationType.SWAGGER_2)
//                .securityContexts(Arrays.asList(securityContext()))
//                .securitySchemes(Arrays.asList(apiKey()))
                .select()
//                .apis(RequestHandlerSelectors.any()) // 특정 패키지경로를 API문서화 한다. 1차 필터
                .apis(RequestHandlerSelectors.basePackage("com.abs.utility")) // 해당 패키지만 필터한다.
                .paths(PathSelectors.any()) // apis중에서 특정 path조건 API만 문서화 하는 2차 필터
                .build()
                ;

        result
            .groupName("API 1.0") // group별 명칭을 주어야 한다.
//                .pathMapping("/")
            .apiInfo(apiInfo())
//            .useDefaultResponseMessages(false) // 400,404,500 .. 표기를 ui에서 삭제한다..
        ;
        if (StringUtils.hasLength(host)) {
            result.host(host);
        }

        return result;
    }


//    private SecurityContext securityContext() {
//        return SecurityContext.builder()
//                .securityReferences(defaultAuth())
//                .build();
//    }
//
//    private List<SecurityReference> defaultAuth() {
//        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
//        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
//        authorizationScopes[0] = authorizationScope;
//        return Arrays.asList(new SecurityReference("access_token", authorizationScopes));
//    }
//    private ApiKey apiKey() {
//        return new ApiKey("access_token", "access_token", "header");
//    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Azure Blob Storage Utility Rest API")
                .description("Azure Blob Storage Utility Rest API")
                .version("1.0")
                .termsOfServiceUrl("")
//                .contact()
                .license("")
                .licenseUrl("")
                .build()
                ;

    }

}