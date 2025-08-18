package com.mertkacar.initializer;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.Deque;

@Component
public class DockerLogStreamer implements ApplicationListener<ApplicationReadyEvent> {

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_YELLOW = "\u001B[33m";

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        // Son 5 log ile başla, sonra canlı takip et
        new Thread(() -> streamLogs("docker compose logs --tail=5 -f")).start();
    }

    private void streamLogs(String command) {
        try {
            ProcessBuilder pb = getProcessBuilder(command);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            Deque<String> buffer = new ArrayDeque<>(5);
            boolean initialBatchPrinted = false;

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    if (!initialBatchPrinted) {
                        if (buffer.size() == 5) {
                            buffer.removeFirst();
                        }
                        buffer.addLast(line);

                        if (buffer.size() == 5) {
                            System.out.println("\n--- Last 5 Docker Logs ---");
                            buffer.forEach(l ->
                                    System.out.println(ANSI_BLUE + "[DOCKER] " + ANSI_YELLOW + l + ANSI_RESET)
                            );
                            initialBatchPrinted = true;
                        }
                    } else {
                        // İlk batch'ten sonra tek tek bas
                        System.out.println(ANSI_BLUE + "[DOCKER] " + ANSI_YELLOW + line + ANSI_RESET);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ProcessBuilder getProcessBuilder(String command) {
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            return new ProcessBuilder("cmd", "/c", command);
        }
        return new ProcessBuilder("bash", "-c", command);
    }
}
