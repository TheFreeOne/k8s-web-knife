package org.freeone.k8s.web.knife.config.websocket.ica10888.config;


import org.freeone.k8s.web.knife.config.websocket.ica10888.ws.SpringWebSocketHandler;
import org.freeone.k8s.web.knife.config.websocket.ica10888.ws.SpringWebSocketHandlerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new SpringWebSocketHandler(), "/container/terminal/shell/ws")
                .setAllowedOrigins()
                .addInterceptors(new SpringWebSocketHandlerInterceptor())
                .setAllowedOrigins("*"); // 添加允许跨域访问
              //  .withSockJS();
    }

    @Bean
    public TextWebSocketHandler webSocketHandler(){
        return new SpringWebSocketHandler();
    }

}
