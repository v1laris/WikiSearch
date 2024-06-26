package com.wks.wikisearch.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition (
        info = @Info(
                title = "WikiSearch API",
                description = "Search on Wikipedia",
                version = "1.0",
                contact = @Contact (
                        name = "Dmitry",
                        email = "shpakdmitry1@gmail.com"
                )
        )
)
public class WikiSearchApiConfig {

}
