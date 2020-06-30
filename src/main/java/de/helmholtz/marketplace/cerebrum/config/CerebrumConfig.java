package de.helmholtz.marketplace.cerebrum.config;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.JettyClientHttpConnector;
import org.springframework.security.oauth2.server.resource.web.reactive.function.client.ServletBearerExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@Configuration
public class CerebrumConfig
{
    /**
     * FIXME: This is workaround  for the Internal Server Error
     * org.springframework.web.util.NestedServletException:
     * Request processing failed; nested exception is
     * java.lang.NullPointerException: Missing SslContextFactory
     */
    private final SslContextFactory ssl = new SslContextFactory();
    private final HttpClient httpClient = new HttpClient(ssl);
    ClientHttpConnector clientConnector = new JettyClientHttpConnector(httpClient);

    @Bean
    public WebClient authorisationServer() {
        return WebClient.builder()
                .filter(new ServletBearerExchangeFilterFunction())
                .clientConnector(clientConnector)
                .build();
    }
}
