package com.mertkacar.starter;

import com.mertkacar.initializer.ComposeInitializer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@ComponentScan("com.mertkacar")
@EnableJpaRepositories("com.mertkacar.repositories")
@EntityScan("com.mertkacar.model")
@SpringBootApplication
public class KafkaredislogApplicationStarter {
  public static void main(String[] args) {
      new SpringApplicationBuilder(KafkaredislogApplicationStarter.class)
              .initializers(new ComposeInitializer())
              .run(args) ;


	}

}
