package scg.test.test;


import scg.test.GatewaySampleApplication;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.SocketUtils;


import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = GatewaySampleApplication.class, webEnvironment = RANDOM_PORT)
public class GatewaySampleApplicationTests {

    @LocalServerPort
    protected int port = 0;

    protected static int managementPort;

    protected WebTestClient webClient;
    protected String baseUri;

    @BeforeClass
    public static void beforeClass() {
        managementPort = SocketUtils.findAvailableTcpPort();
        System.setProperty("management.server.port", String.valueOf(managementPort));
    }

    @AfterClass
    public static void afterClass() {
        System.clearProperty("management.server.port");
    }

    @Before
    public void setup() {
        baseUri = "http://localhost:" + port;
        this.webClient = WebTestClient.bindToServer().baseUrl(baseUri).build();
    }

    @Test
    public void contextLoads() {
        EntityExchangeResult<byte[]> result =

                webClient.get()
                .uri("/")
                .exchange()
                .expectBody().returnResult();

        String respBod = new String(result.getResponseBodyContent());

        System.out.println("\n\nURL: " + result.getUrl());
        System.out.println("RESPONSE HEADERS: " + result.getResponseHeaders().toString());
        System.out.println("RESPONSE BODY: " + respBod);

    }

}