//package org.freeone.k8s.web.knife.config.websocket.ica10888.config;
//
//import io.kubernetes.client.openapi.ApiClient;
//import io.kubernetes.client.util.ClientBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//
//@Configuration
//public class KubeConfig {
//
//    @Bean(name = "kubeApiClient")
//    public ApiClient apiClient() {
////        ApiClient kubeApiClient = null;
////        try {
////            kubeApiClient = Config.fromConfig(configPath);
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
////        kubeApiClient.setConnectTimeout(1800000);
////        kubeApiClient.setReadTimeout(1800000);
////        kubeApiClient.setWriteTimeout(1800000);
////
////        io.kubernetes.client.openapi.Configuration.setDefaultApiClient(kubeApiClient);
//
//
//        ApiClient apiClient = new ClientBuilder().setBasePath("https://192.168.110.130:6443")
//                .setVerifyingSsl(false)
//                .build();
//        apiClient.addDefaultHeader("Authorization", "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6InhfLXBxNDlzYUU3Y21velBySVc4ZnlfZDVKVWp6c0dyUS1ONDJINk9icW8ifQ.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJrdWJlLXN5c3RlbSIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VjcmV0Lm5hbWUiOiJzZWNyZXQtYWRtaW4iLCJrdWJlcm5ldGVzLmlvL3NlcnZpY2VhY2NvdW50L3NlcnZpY2UtYWNjb3VudC5uYW1lIjoiYWRtaW4iLCJrdWJlcm5ldGVzLmlvL3NlcnZpY2VhY2NvdW50L3NlcnZpY2UtYWNjb3VudC51aWQiOiJiZjg0YWJlNC0zYTZhLTQ4NDEtYmMwOC0xODczN2NlYjNiNzAiLCJzdWIiOiJzeXN0ZW06c2VydmljZWFjY291bnQ6a3ViZS1zeXN0ZW06YWRtaW4ifQ.MVXr2hUgOdUXDZ-1Ne_DlEQ9ODEia_usMqDBSOa9Yv5tLe3KMcfeC6Qo_UVmaIs7OX2h2Ug3aCO1Ie3-QssAeeZsZqDzH-C0674LGgME1RvY5ZQ193LGZaaECRf9WU78sQIwYBY8TZQWJhDSgq25YlKaiHyLShKVaIzQyLuTKmihDFVe2pz3FLFvSyXcDJ-dBorUMItGiHTGxYcf8AumP9Tfc8Zf7S9ILrLbTyPWAEJLKfAm24dvgNvOmJm4T8QUdAf3PMsR4sbZovdvCz9u_Pu6ArvAoO1G-ltG4C4KpLvqY_WGxZNw2oiuqls_6Xfl-UiaPzQs3OFzHIehZOOtWw");
//
//        apiClient.setConnectTimeout(1800000);
//        apiClient.setReadTimeout(1800000);
//        apiClient.setWriteTimeout(1800000);
//        io.kubernetes.client.openapi.Configuration.setDefaultApiClient(apiClient);
//        return apiClient;
//    }
//
//}
