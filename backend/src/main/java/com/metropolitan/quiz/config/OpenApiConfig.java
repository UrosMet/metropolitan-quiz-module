package com.metropolitan.quiz.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Metropolitan Quiz Module API",
                version = "1.0.0",
                description = "API za mini LMS modul za rad sa kvizovima, namenjen nastavnicima i studentima.",
                contact = @Contact(
                        name = "Metropolitan univerzitet"
                )
        ),
        security = {
                @SecurityRequirement(name = "userIdHeader"),
                @SecurityRequirement(name = "userRoleHeader")
        }
)
@SecurityScheme(
        name = "userIdHeader",
        type = SecuritySchemeType.APIKEY,
        in = SecuritySchemeIn.HEADER,
        paramName = "X-User-Id",
        description = "Simulirani ID korisnika. Primer: 1 za nastavnika, 2 ili 3 za studente."
)
@SecurityScheme(
        name = "userRoleHeader",
        type = SecuritySchemeType.APIKEY,
        in = SecuritySchemeIn.HEADER,
        paramName = "X-User-Role",
        description = "Simulirana uloga korisnika. Dozvoljene vrednosti su: TEACHER ili STUDENT."
)
public class OpenApiConfig {
}