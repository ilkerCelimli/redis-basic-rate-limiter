package com.portifolyo.redisbasicratelimiter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Date;
import java.util.Optional;

@Component
public class RateLimiterFilter extends OncePerRequestFilter {

    private final IpRateLimiterRepository ipRateLimiterRepository;

    public RateLimiterFilter(IpRateLimiterRepository ipRateLimiterRepository) {
        this.ipRateLimiterRepository = ipRateLimiterRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String ip = request.getRemoteAddr();

        Optional<IpRateLimiter> ipRateLimiter = ipRateLimiterRepository.findByIp(ip);
        if (ipRateLimiter.isEmpty()) {
            IpRateLimiter newIpRateLimiter = new IpRateLimiter(ip, 1, false, new Date(), new Date());
            this.ipRateLimiterRepository.save(newIpRateLimiter);
            filterChain.doFilter(request, response);
            return;
        }

        IpRateLimiter ipRateLimiterValue = ipRateLimiter.get();

        if(ipRateLimiterValue.isBlocked()){
            response.sendError(429, "Too Many Requests");
            return;
        }

        if(ipRateLimiterValue.getLastRequest().getTime() - new Date().getTime() < 1000){
            response.sendError(429, "Too Many Requests");
            return;
        }

        if(ipRateLimiterValue.getRate() > 5) {
            ipRateLimiterValue.setLastRequest(new Date());
            ipRateLimiterValue.setBlocked(true);
            this.ipRateLimiterRepository.save(ipRateLimiterValue);
            filterChain.doFilter(request,response);
            return;
        }

        ipRateLimiterValue.setLastRequest(new Date());
        ipRateLimiterValue.setRate(ipRateLimiterValue.getRate()+1);
        this.ipRateLimiterRepository.save(ipRateLimiterValue);
        filterChain.doFilter(request,response);
        }
    }
