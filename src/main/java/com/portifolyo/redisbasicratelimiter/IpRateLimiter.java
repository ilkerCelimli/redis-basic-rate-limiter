package com.portifolyo.redisbasicratelimiter;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.Date;

@RedisHash("ip-rate-limiter")
public class IpRateLimiter {

    @Id
    private String ip;
    private int rate;
    private boolean isBlocked = Boolean.FALSE;

    private Date firstRequest;
    private Date lastRequest;


    public IpRateLimiter(String ip, int rate, boolean isBlocked, Date firstRequest, Date lastRequest) {
        this.ip = ip;
        this.rate = rate;
        this.isBlocked = isBlocked;
        this.firstRequest = firstRequest;
        this.lastRequest = lastRequest;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    public void setFirstRequest(Date firstRequest) {
        this.firstRequest = firstRequest;
    }

    public void setLastRequest(Date lastRequest) {
        this.lastRequest = lastRequest;
    }

    public String getIp() {
        return ip;
    }

    public int getRate() {
        return rate;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public Date getFirstRequest() {
        return firstRequest;
    }

    public Date getLastRequest() {
        return lastRequest;
    }
}

