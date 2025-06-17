package com.techie.microservice.order;

import com.mysql.cj.MysqlConnection;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.techie.microservice.order.stubs.InventoryClientStub;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.shaded.org.hamcrest.Matchers;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
class OrderServiceApplicationTests {

	@ServiceConnection
	static MySQLContainer mySQLContainer = new MySQLContainer("mysql:8.3.0");

	// the problem is that service call the reel api of inventory
	// we have to mock the interaction between the services
	// we use Wiremock to mock the entire api ==! mockito mock just the method (no rest api test)
    // principe mock isInStock() api call using wiremock

	@LocalServerPort
	private Integer port;

	@BeforeEach()
	void setup(){
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = port;
	}

	static {
		mySQLContainer.start();
	}

	@Test
	void shouldSubmitOrder() {
		String submitOrderJson = """
				{
				    "orderNumber": "o_1",
				    "skuCode": "ipixel_8",
				    "price": 120,
				     "quantity": 100
				}
				""";

		InventoryClientStub.stubInventoryCall("ipixel_8",100);

		var responseBodyString = RestAssured.given()
				.contentType("application/json")
				.body(submitOrderJson)
				.when()
				.post("/api/order")
				.then().log().all()
				.statusCode(201)
				.extract()
				.body().asString();


		assertEquals("Order placed Successfully", responseBodyString);
	}

}
