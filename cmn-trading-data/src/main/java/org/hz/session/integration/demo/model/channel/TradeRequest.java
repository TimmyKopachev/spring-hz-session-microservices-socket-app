package org.hz.session.integration.demo.model.channel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TradeRequest {

    private String product;
    private String usernameTo;
}
