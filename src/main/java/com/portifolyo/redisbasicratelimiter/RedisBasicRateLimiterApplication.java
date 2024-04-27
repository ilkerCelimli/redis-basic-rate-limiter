package com.portifolyo.redisbasicratelimiter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@EnableRedisRepositories
@EnableWebSecurity
@EnableScheduling
@EnableAsync
public class RedisBasicRateLimiterApplication {

	private final IpRateLimiterRepository repository;

    public RedisBasicRateLimiterApplication(IpRateLimiterRepository repository) {
        this.repository = repository;
    }

    public static void main(String[] args) {
		SpringApplication.run(RedisBasicRateLimiterApplication.class, args);
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http, RateLimiterFilter rateLimiterFilter) throws Exception {
		return http.cors(AbstractHttpConfigurer::disable)
				.csrf(AbstractHttpConfigurer::disable)
				.authorizeHttpRequests(i -> i.requestMatchers("/**").permitAll())
				.addFilterBefore(rateLimiterFilter, UsernamePasswordAuthenticationFilter.class)
				.build();
	}

	@Scheduled(timeUnit = TimeUnit.SECONDS, fixedDelay = 15)
	public void resetIpRateLimiter() {
		Iterable<IpRateLimiter> ipRateLimiters = repository.findAll();
		List<IpRateLimiter> list = new ArrayList<>();
		for(IpRateLimiter ipRateLimiter : ipRateLimiters){
			if(ipRateLimiter.getFirstRequest().getTime() < new Date().getTime() - 5000){
				list.add(ipRateLimiter);
			}
		}
		repository.deleteAll(list);
		System.out.println("Reset IpRateLimiter");
	}
}