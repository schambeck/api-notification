package com.schambeck.notification.config;

import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.*;

import static org.springframework.web.filter.AbstractRequestLoggingFilter.*;

@Configuration
class RequestLoggingFilterConfig {

    @Bean
    @SneakyThrows
    CommonsRequestLoggingFilter logFilter() {
        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
        filter.setIncludeQueryString(true);
        filter.setIncludePayload(true);
        filter.setMaxPayloadLength(10000);
        filter.setIncludeHeaders(false);
        filter.setIncludeClientInfo(true);
        filter.setBeforeMessagePrefix(DEFAULT_BEFORE_MESSAGE_PREFIX + "\n" +
                getLocalHostAddressDescription() +
                getIpsFromAllInterfacesDescription());
        return filter;
    }

    String getLocalHostAddressDescription() {
        return "LocalHostAddress: " + getLocalHostAddress() + "\n";
    }

    @SneakyThrows
    String getLocalHostAddress() {
        return InetAddress.getLocalHost().getHostAddress();
    }

    String getIpsFromAllInterfacesDescription() {
        StringBuilder description = new StringBuilder();
        Map<String, List<String>> ipsFromAllInterfaces = getIpsFromAllInterfaces();
        for (String networkInterface : ipsFromAllInterfaces.keySet()) {
            List<String> ips = ipsFromAllInterfaces.get(networkInterface);
            for (String ip : ips) {
                description.append(networkInterface)
                        .append(": ")
                        .append(ip)
                        .append(System.lineSeparator());
            }
        }
        return description.toString();
    }
    @SneakyThrows
    Map<String, List<String>> getIpsFromAllInterfaces() {
        Map<String, List<String>> ipAddresses = new HashMap<>();
        Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
        for (NetworkInterface networkInterface: Collections.list(nets)) {
            List<String> addresses = new ArrayList<>();
            Enumeration<InetAddress> ips = networkInterface.getInetAddresses();
            for (InetAddress inetAddress : Collections.list(ips)) {
                addresses.add(inetAddress.getHostAddress());
            }
            ipAddresses.put(networkInterface.getDisplayName(), addresses);
        }
        return ipAddresses;
    }

}
