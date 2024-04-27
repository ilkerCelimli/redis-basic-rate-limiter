package com.portifolyo.redisbasicratelimiter;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface IpRateLimiterRepository extends CrudRepository<IpRateLimiter, String> {

    Optional<IpRateLimiter> findByIp(String ip);
}
