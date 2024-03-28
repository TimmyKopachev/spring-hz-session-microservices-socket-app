package org.hz.session.integration.demo.endpoint;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.hz.session.integration.demo.model.channel.TradeRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Set;

@Controller
@Slf4j
@RequestMapping("/trading-ui")
public class TradingEventController {

    @Autowired
    private KafkaTemplate<String, TradeRequest> playerProductTradingKafkaTemplate;
    @Value("${trading.init.topics}")
    private String tradingTopic;

    @GetMapping
    public String submitTrading(Model model) {
        model.addAttribute("products", generateInitialProducts());
        return "index";
    }

    @GetMapping("/{product}/{receiver}")
    public String submitTrading(@PathVariable String product,
                                @PathVariable String receiver,
                                Model model) {
        playerProductTradingKafkaTemplate.send(tradingTopic, receiver, new TradeRequest(product, receiver));
        model.addAttribute("products", generateInitialProducts());
        return "index";
    }

    private Set<String> generateInitialProducts() {
        return Set.of("arcana-SF", "arcana-PA");
    }
}
