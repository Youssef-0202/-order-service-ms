package com.techie.microservice.order.stubs;

/**
 * @author HP
 **/

import static  com.github.tomakehurst.wiremock.client.WireMock.*;
public class InventoryClientStub {

    public static void stubInventoryCall(String skuCode , Integer quantity){
        stubFor(get(urlEqualTo("/api/inventory?skuCode="+skuCode+"&quantity="+quantity))
                .willReturn(aResponse()
                        .withBody("true")
                        .withHeader("Content-Type","application/json")
                        .withStatus(200)
                )
        );
    }
}
