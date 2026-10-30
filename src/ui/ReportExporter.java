package ui;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class ReportExporter {
    private static final DateTimeFormatter FILE_DATE =
            DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

    private ReportExporter() {
    }

    public static Path export(String reportType, String content) throws IOException {
        if (reportType == null || !reportType.matches("[A-Za-z0-9]+") || content == null) {
            throw new IllegalArgumentException("Report type and content are required.");
        }
        Path directory = Path.of("reports");
        Files.createDirectories(directory);
        String baseName = LocalDateTime.now().format(FILE_DATE) + "_" + reportType;
        Path report = directory.resolve(baseName + ".txt");
        int suffix = 1;
        while (Files.exists(report)) {
            report = directory.resolve(baseName + "_" + suffix++ + ".txt");
        }
        Files.writeString(report, content, StandardCharsets.UTF_8);
        return report;
    }
}
