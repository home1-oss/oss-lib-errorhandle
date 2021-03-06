package cn.home1.oss.lib.errorhandle.mockmvc;

import cn.home1.oss.lib.errorhandle.starter.ErrorHandleAutoConfiguration;

import cn.home1.oss.lib.webmvc.starter.WebApplicationAutoConfiguration;

import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@MinimalWebConfiguration
@Import({ErrorHandleAutoConfiguration.class, WebApplicationAutoConfiguration.class})
@ComponentScan
public class ErrorhandleMockMvcConfiguration {

  // for manual test
  public static void main(final String... args) {
    SpringApplication.run(ErrorhandleMockMvcConfiguration.class, args);
  }
}
