package scg.test;

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ModifyBodyGatewayFilterImpl implements GatewayFilter {


    private static Logger logger = LoggerFactory.getLogger(ModifyBodyGatewayFilterImpl.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {


        ServerHttpResponse response = exchange.getResponse();
        DataBufferFactory dataBufferFactory = response.bufferFactory();

        ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(response) {

            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                if (body instanceof Flux) {
                    Flux<? extends DataBuffer> flux = (Flux<? extends DataBuffer>) body;


                    return super.writeWith(flux.buffer().map(b -> {


                        /*
                        *       The buffer will have 300 bytes with the example address
                        *       provided in the test case.
                        *
                         */
                        ByteOutputStream outputStream = new ByteOutputStream();
//                        b.forEach( buffer -> {
//                            byte[] array = new byte[buffer.readableByteCount()];
//                            buffer.read(array);
//                            outputStream.write(array);
//                        });

                        /*
                        *       Assume that the new response needs to be 325 bytes.
                        *       You will see that it will only ever have 300.
                        *
                         */
                        StringBuilder sb = new StringBuilder();
                        for(int i = 0; i< 300; i++) {
                            sb.append("0");
                        }

                        byte[] threeHundredBytes = sb.toString().getBytes();

                        System.err.println("threeHundredBytes::: " + threeHundredBytes.length);

                        outputStream.write(threeHundredBytes);

                        /*
                        *       Now trying to add an additional 10
                        *       You'll notice none of these are in the response.
                        *
                         */
                        StringBuilder sb2 = new StringBuilder();
                        for(int i = 0; i< 10; i++) {
                            sb.append("1");
                        }

                        outputStream.write(sb2.toString().getBytes());


                        return dataBufferFactory.wrap(outputStream.getBytes());
                    }));
                }
                return super.writeWith(body);
            }
        };

        ServerWebExchange swe = exchange.mutate().response(decoratedResponse).build();
        return chain.filter(swe);
    }


}
