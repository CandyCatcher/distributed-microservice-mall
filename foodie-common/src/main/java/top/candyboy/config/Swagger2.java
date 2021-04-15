package top.candyboy.config;

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

// 配置Swagger2
// 被spring扫描到
@Configuration
@EnableSwagger2
public class Swagger2 {

    // http://localhost:8088/swagger-ui.html
    // http://localhost:8088/doc.html

    // 配置 swagger2核心配置 docket
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)  // 指定api类型为swagger2
                .apiInfo(apiInfo())     //用于定义api文档汇总信息
                .select()
                .apis(RequestHandlerSelectors.basePackage("top.candyboy.controller"))  // 指定controller包
                .paths(PathSelectors.any())  // 指定所有的controller
                .build();
    }

    public ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("分布式的电商平台")                  // 文档页标题
                .contact(new Contact("candyboy",   // 开发人员联系方式
                        "candyboy.top",
                        "aihead@126.com"))
                .description("为电商提供的API文档")         // 详细信息
                .version("1.0.1")                        // 文档版本号
                .termsOfServiceUrl("candyboy.top")
                .build();
    }
}
