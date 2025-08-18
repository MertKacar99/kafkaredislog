package com.mertkacar.initializer;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;
import java.net.Socket;
import java.util.LinkedHashMap;
import java.util.Map;

public class ComposeInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {

        // Uygulama kapatıldığında docker stop çeker
        Runtime.getRuntime().addShutdownHook(new Thread(() -> runCommand("docker compose stop", true)));

        // Servisleri durdur
        runCommand("docker compose stop", true);

        // Servisleri başlat
        runCommand("docker compose up -d", true);

        // Servis listesi
        Map<String, Integer> services = new LinkedHashMap<>();
        services.put("postgres", 5432);
        services.put("redis", 6379);
        services.put("kafka", 9094);

        // Servislerin hazır olmasını bekle
        services.forEach((service, port) -> waitForService("localhost", port, 60, service));
    }

    private void runCommand(String command, boolean wait) {
        try {
            ProcessBuilder pb = getProcessBuilder(command);
            pb.redirectErrorStream(true); // stdout+stderr birleştir
            Process process = pb.start();

            if (wait) {
                process.waitFor();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private ProcessBuilder getProcessBuilder(String command) {
        if (System.getProperty("os.name").toLowerCase().contains("win"))
            return new ProcessBuilder("cmd", "/c", command);
        return new ProcessBuilder("bash", "-c", command);
    }

    private void waitForService(String host, int port, int timeoutSeconds, String serviceName) {
        long start = System.currentTimeMillis();
        System.out.println(serviceName + " bekleniyor...");
        while (System.currentTimeMillis() - start < timeoutSeconds * 1000L) {
            try (Socket socket = new Socket(host, port)) {
                System.out.println(serviceName + " çalışıyor.");
                return;
            } catch (IOException e) {
                try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
            }
        }
        throw new RuntimeException(serviceName + " belirtilen sürede başlamadı!");
    }
}
