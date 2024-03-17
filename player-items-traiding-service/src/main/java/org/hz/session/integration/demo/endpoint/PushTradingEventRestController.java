package org.hz.session.integration.demo.endpoint;

import lombok.extern.slf4j.Slf4j;
import org.hz.session.integration.demo.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@Slf4j
@RequestMapping("/trading")
public class PushTradingEventRestController {

    @Autowired
    private KafkaTemplate<String, String> playerProductTradingKafkaTemplate;
    @Autowired
    private PlayerService playerService;

    @Value("${trading.init.topics}")
    private String tradingTopic;

    @GetMapping("{product}")
    public void submitTrading(@PathVariable String product) {
        var player = playerService.getCurrentAuthenticatedPlayerUser();
        Objects.requireNonNull(player);
        playerProductTradingKafkaTemplate
                .send(tradingTopic, player.getUsername(), product)
                .exceptionally(
                        exception -> {
                            log.error("Issue occurred during request pushing notification by:{} due to <{}>",
                                    player.getUsername(), exception.getMessage());
                            return null;
                        })
                .thenApply(result -> !Objects.nonNull(result));
    }
}
