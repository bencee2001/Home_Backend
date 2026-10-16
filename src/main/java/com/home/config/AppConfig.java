package com.home.config;

import com.zaxxer.hikari.HikariDataSource;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.docs.DocDocket;
import org.noear.solon.docs.models.ApiInfo;

import javax.sql.DataSource;

@Configuration
public class AppConfig {

    // Binds the "app.db" section of app.yml onto a HikariDataSource; Wood picks it up via @Db("db")
    @Bean(name = "db", typed = true)
    public DataSource db(@Inject("${app.db}") HikariDataSource ds) {
        return ds;
    }

    // OpenAPI 3 group, browsable at /doc.html
    @Bean("api")
    public DocDocket api() {
        return new DocDocket()
                .groupName("api")
                .info(new ApiInfo().title("Home Backend API").version("1.0.0"))
                .apis("com.home.controller");
    }
}
