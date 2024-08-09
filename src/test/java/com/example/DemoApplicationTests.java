package com.example;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.Test;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.testcontainers.chromadb.ChromaDBContainer;
import org.testcontainers.ollama.OllamaContainer;
import org.testcontainers.utility.DockerImageName;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
public class DemoApplicationTests {

	@Autowired
	EmbeddingModel model;

	public static void main(String[] args) {
		SpringApplication.from(DemoApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

	@Test
	public void contextLoads() {
		System.err.println(model.embed("Hello, world!"));
	}

}

@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {

	private static final Log logger = LogFactory.getLog(TestcontainersConfiguration.class);

	@Bean
	@ServiceConnection
	public ChromaDBContainer chroma() {
		return new ChromaDBContainer(DockerImageName.parse("ghcr.io/chroma-core/chroma:0.5.5"));
	}

	@Bean
	@ServiceConnection
	public OllamaContainer ollama() throws Exception {
		OllamaContainer ollama = new OllamaContainer(DockerImageName.parse("ollama/ollama:0.3.2"));
		return ollama;
	}

	@Bean
	ApplicationRunner runner(OllamaContainer ollama) {
		return args -> {
			logger.info("Pulling models...");
			ollama.execInContainer("ollama", "pull", "albertogg/multi-qa-minilm-l6-cos-v1");
			ollama.execInContainer("ollama", "pull", "mistral");
			logger.info("...done");
		};
	}

}