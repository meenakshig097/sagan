package org.springframework.test.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.bootstrap.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.site.configuration.ApplicationConfiguration;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

public class StaticAssetRequestMappingTest {

	private static ConfigurableApplicationContext context;

	@BeforeClass
	public static void start() throws Exception {
		Future<ConfigurableApplicationContext> future = Executors
				.newSingleThreadExecutor().submit(
						new Callable<ConfigurableApplicationContext>() {
							@Override
							public ConfigurableApplicationContext call() throws Exception {
								return (ConfigurableApplicationContext) SpringApplication
										.run(ApplicationConfiguration.class);
							}
						});
		context = future.get(30, TimeUnit.SECONDS);
	}

	@AfterClass
	public static void stop() {
		if (context != null) {
			context.close();
		}
	}

	@Test
	public void getStyleSheet() throws Exception {
		ResponseEntity<String> response = getRestTemplate().getForEntity(
				"http://localhost:8080/css/getting-started.css", String.class);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertTrue(response.getHeaders().getContentType()
				.isCompatibleWith(MediaType.valueOf("text/css")));
		assertTrue(response.getBody().contains("h1,h2,h3,h4,h5,h6"));
	}

	private RestTemplate getRestTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
			@Override
			public boolean hasError(ClientHttpResponse response) throws IOException {
				return false;
			}
		});
		return restTemplate;
	}

}