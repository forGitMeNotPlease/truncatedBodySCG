package scg.test;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@SpringBootConfiguration
@EnableAutoConfiguration
@Configuration
public class GatewaySampleApplication {


    @Bean("modifyBodyGatewayFilterImpl")
    public ModifyBodyGatewayFilterImpl modifyBodyGatewayFilterImpl() {
        return new ModifyBodyGatewayFilterImpl();
    }

    @Bean
    public RouteLocator customRouteLocator2(RouteLocatorBuilder builder,
                                           @Qualifier("modifyBodyGatewayFilterImpl")
                                                   ModifyBodyGatewayFilterImpl modifyBodyGatewayFilterImpl) {

        RouteLocator routeLocator = builder.routes()
                .route(r -> r.path("/")

                    .filters(f -> {
                        return f.filter(modifyBodyGatewayFilterImpl, -2)
                                .rewritePath("/", "/get");
                    })
                      .uri("http://httpbin.org:80")

                )
                .build();

        return routeLocator;
    }

    public static void main(String[] args) {
        SpringApplication.run(GatewaySampleApplication.class, args);
    }
}