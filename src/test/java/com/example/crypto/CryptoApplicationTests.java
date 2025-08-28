package com.example.crypto;


import com.example.crypto.dto.CryptoMinMaxDto;
import com.example.crypto.dto.CryptoOldNewDto;
import com.example.crypto.dto.CryptoStatsDto;
import com.example.crypto.exception.UnsupportedCryptoException;
import com.example.crypto.repository.CryptoPriceRepository;
import com.example.crypto.service.CryptoPriceDataSchedular;
import com.example.crypto.service.CryptoPriceService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@SpringBootTest
class CryptoApplicationTests {


	@Mock
	private CryptoPriceRepository cryptoPriceRepository;

	@InjectMocks
	private CryptoPriceService cryptoPriceService;

	@BeforeEach
	void setup() {
		try (AutoCloseable mocks = MockitoAnnotations.openMocks(this)) {
		} catch (Exception e) {
			throw new RuntimeException("Failed to initialize mocks", e);
		}
	}



	@Test
	void testGetCryptoStats_validSymbol_returnsStats() {
		String symbol = "BTC";

		Mockito.when(cryptoPriceRepository.findMinMaxPriceBySymbol(symbol))
				.thenReturn(Optional.of(new CryptoMinMaxDto(symbol,
						new BigDecimal("100.00"),
						new BigDecimal("200.00"))));
		Mockito.when(cryptoPriceRepository.findOldAndNewPriceBySymbol(symbol))
				.thenReturn(Optional.of(new CryptoOldNewDto(symbol,
						new BigDecimal("120.00"),
						new BigDecimal("180.00"))));

		CryptoStatsDto result = cryptoPriceService.getCryptoStats(symbol);

		Assertions.assertEquals(symbol, result.getSymbol());
		Assertions.assertEquals(new BigDecimal("100.00"), result.getMinPrice());
		Assertions.assertEquals(new BigDecimal("200.00"), result.getMaxPrice());
		Assertions.assertEquals(new BigDecimal("120.00"), result.getOldestPrice());
		Assertions.assertEquals(new BigDecimal("180.00"), result.getNewestPrice());
	}

	@Test
	void testGetCryptoStats_UnsupportedSymbol() {

		String invalidSymbol = "XYZ123";
		List<String> validSymbols = Arrays.asList("BTC", "DOGE", "ETH");
		Mockito.when(cryptoPriceRepository.findAllDistinctSymbols())
				.thenReturn(validSymbols);

		UnsupportedCryptoException exception = Assertions.assertThrows(UnsupportedCryptoException.class,
				() -> cryptoPriceService.getCryptoStats(invalidSymbol));

		Assertions.assertEquals("Crypto symbol not supported: XYZ123", exception.getMessage());

	}


}
