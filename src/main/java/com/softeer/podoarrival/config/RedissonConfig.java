package com.softeer.podoarrival.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    @Value("${secret.redis-url}")
    private String redisUrl;

    @Bean
    public RedissonClient redisson() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress(redisUrl);

        // 인코딩 코덱 설정
        Codec codec = new StringCodec();
        config.setCodec(codec);

        return Redisson.create(config);
    }
}
