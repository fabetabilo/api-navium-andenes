package com.navium.andenes;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApiAndenesApplicationTests {

	@Disabled("Requiere conexion a base de datos; no aplica en CI")
	@Test
	void contextLoads() {
	}

}
