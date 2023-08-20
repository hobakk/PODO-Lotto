package com.example.sixnumber.fixture;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import com.example.sixnumber.global.dto.UnifiedResponse;

public class TestUtil {
	public static <T> void UnifiedResponseEquals(UnifiedResponse<T> response, int code, String msg) {
		assertEquals(response.getMessage(), msg);
		assertEquals(response.getCode(), code);
	}

	public static <T> void UnifiedResponseEquals(UnifiedResponse<T> response, int code, String msg, Class<T> clazz) {
		assertEquals(response.getMessage(), msg);
		assertEquals(response.getCode(), code);
		if (clazz != null) assertNotNull(response.getData());
	}

	public static <T> void UnifiedResponseListEquals(UnifiedResponse<List<T>> response, int code, String msg) {
		assertEquals(response.getMessage(), msg);
		assertEquals(response.getCode(), code);
		assertNotNull(response.getData());
	}
}
