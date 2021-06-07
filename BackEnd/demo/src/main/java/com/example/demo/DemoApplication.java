package com.example.demo;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.apache.catalina.connector.Connector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * The type Demo application.
 */
@SpringBootApplication
public class DemoApplication {

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     * @throws IOException the io exception
     */
    public static void main(String[] args) throws IOException {
		SpringApplication.run(DemoApplication.class, args);
		firebaseInitializer();
		System.out.println("Spring Server Started....");
	}

	private static void firebaseInitializer() throws IOException {
		try {
			FirebaseOptions firebaseOptions;
			firebaseOptions = new FirebaseOptions.Builder()
					.setCredentials(GoogleCredentials.getApplicationDefault())
					.setDatabaseUrl("https://bigmoves-1567916009740.firebaseio.com/")
					.build();
			FirebaseApp.initializeApp(firebaseOptions);
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}

	@Configuration
	public class HttpConfig {
		@Value("${server.http.port}")
		private int httpPort;

		@Bean // (it only works for springboot 2.x)
		public ServletWebServerFactory servletContainer() {
			TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
			factory.addAdditionalTomcatConnectors(createStanderConnecter());
			return factory;
		}

		private Connector createStanderConnecter() {
			Connector connector =
					//new Connector("org.apache.coyote.http11.Http11NioProtocol");
					new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
			connector.setPort(httpPort);
			return connector;
		}
	}



	}
