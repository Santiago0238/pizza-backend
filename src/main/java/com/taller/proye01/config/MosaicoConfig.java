package com.taller.proye01.config;

import com.github.benmanes.caffeine.cache.Caffeine;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class MosaicoConfig {

	@Bean
	public RestTemplate restTemplate() {

		return new RestTemplate();
	}

	@Bean
	public CacheManager cacheManager() {

		// =====================================================
		// EOX
		// =====================================================

		var teselasEox = Caffeine.newBuilder().maximumSize(20_000).expireAfterWrite(30, TimeUnit.DAYS).build();

		// =====================================================
		// SENTINEL HUB
		// =====================================================

		/*
		 * Reducimos la duración porque las imágenes actuales pueden actualizarse.
		 *
		 * La fecha forma parte de la clave, por lo que diferentes fechas siguen
		 * teniendo cachés independientes.
		 */

		var teselasSentinelHub = Caffeine.newBuilder().maximumSize(30_000).expireAfterWrite(6, TimeUnit.HOURS).build();

		// =====================================================
		// DISPONIBILIDAD
		// =====================================================

		var disponibilidad = Caffeine.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES).build();

		SimpleCacheManager manager = new SimpleCacheManager();

		manager.setCaches(List.of(

				new CaffeineCache("eoxTiles", teselasEox),

				new CaffeineCache("sentinelHubTiles", teselasSentinelHub),

				new CaffeineCache("sentinelHubDisponible", disponibilidad)));

		manager.afterPropertiesSet();

		return manager;
	}
}