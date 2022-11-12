package com.spsgame;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@Configuration
public class GameConfiguration {
    static {
        io.swagger.v3.core.jackson.ModelResolver.enumsAsRef = true;
    }
    @Autowired
    private RedisTemplate<Long, Object> redisTemplate;

    @Autowired
    private PlayerRepository playerRepository;

    @Bean
    public OpenAPI customOpenAPI(@Value("${app.description}") String appDescription,
                                 @Value("${app.version}") String appVersion) {
        return new OpenAPI().info(new Info().title("Rock, Scissors and Paper Game").version(appVersion)
                        .description(appDescription).termsOfService("http://swagger.io/terms/")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }

    @Bean
    public RedisTemplate<Long, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<Long, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        return template;
    }

    /** Loads computationally intensive values to the cache. These values can later be read by the application. */
    @Scheduled(initialDelay = 0, fixedDelay = 10_000)
    public void fetchStatistics() {
        try {
            log.debug("Loading values to the cache");
            redisTemplate.opsForValue().set(GameService.TOTAL_GAMES_CACHING_ID, playerRepository.sumTimesPlayed());
            redisTemplate.opsForValue().set(GameService.DISTINCT_PLAYERS_CACHING_ID, playerRepository.count());
        } catch(Exception ex){
            log.error("Error fetching statistics caused by {}, the scheduling will be stopped", ex.getMessage(), ex);
            throw new IllegalStateException(ex); // stop automatic execution
        }
    }

}
