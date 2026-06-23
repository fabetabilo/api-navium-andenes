package com.navium.andenes;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Disabled("Requiere conexion a base de datos; no aplica en CI")
@SpringBootTest
class ApiAndenesApplicationTests {

	@Test
	void contextLoads() {
	}

}
