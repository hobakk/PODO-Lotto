package com.example.sixnumber.fixture;

import static org.junit.jupiter.api.Assertions.*;

import com.example.sixnumber.global.dto.ApiResponse;
import com.example.sixnumber.global.dto.ItemApiResponse;
import com.example.sixnumber.global.dto.ListApiResponse;

public class TestUtil {
	public static void ApiAsserEquals(ApiResponse response, int code, String msg) {
		assertEquals(response.getMsg(), msg);
		assertEquals(response.getCode(), code);
	}
	public static <T> void ItemApiAssertEquals(ItemApiResponse<T> response, int code, String msg) {
		assertEquals(response.getMsg(), msg);
		assertEquals(response.getCode(), code);
		assertNotNull(response.getData());
	}
	public static <T> void ListApiAssertEquals(ListApiResponse<T> response, int code, String msg) {
		assertEquals(response.getMsg(), msg);
		assertEquals(response.getCode(), code);
		assertNotNull(response.getData());
	}
}
