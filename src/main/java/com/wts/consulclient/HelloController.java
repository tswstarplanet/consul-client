package com.wts.consulclient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("/user")
public class HelloController {

    private RestTemplate restTemplate;

    @Value("${service-url.cloud-consul-demo}")
    private String url;

    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private LoadBalancerClient loadBalancer;

    public String serviceUrl() {
        List<ServiceInstance> list = discoveryClient.getInstances("STORES");
        if (list != null && list.size() > 0 ) {
            return list.get(0).getUri().toString();
        }
        return null;
    }

    public RestTemplate getRestTemplate() {
        return restTemplate;
    }

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/id")
    public String getId() {
        String res = restTemplate.getForObject(url + "/user/id", String.class);
        System.out.println("res: " + res);
        return res;
    }

    @GetMapping("/id2")
    public String getId2() {
        String url = serviceUrl();
        String res = restTemplate.getForObject(url + "/user/id", String.class);
        System.out.println("res: " + res);
        return res;
    }

    @GetMapping("/test")
    public String test() {
        return "test";
    }

    /**
     * 从所有服务中选择一个服务（轮询）
     */
    @RequestMapping("/discover")
    public Object discover() {
        return loadBalancer.choose("myprefix-cloud-consul-demo").getUri().toString();
    }

    @RequestMapping("/id3")
    public String id3() {
        String url = loadBalancer.choose("myprefix-cloud-consul-demo").getUri().toString();
        String res = restTemplate.getForObject(url + "/user/id", String.class);
        return res;
    }
}
