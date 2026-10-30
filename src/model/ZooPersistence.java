package model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ZooPersistence {
    private static final Path STATE_FILE = Path.of("data", "zoocare.ser");

    private ZooPersistence() {
    }

    public static Zoo load() throws IOException, ClassNotFoundException {
        if (!Files.exists(STATE_FILE)) {
            return null;
        }
        try (ObjectInputStream input = new ObjectInputStream(Files.newInputStream(STATE_FILE))) {
            Object state = input.readObject();
            if (!(state instanceof Zoo)) {
                throw new IOException("The saved data does not contain a valid ZooCare state.");
            }
            return (Zoo) state;
        }
    }

    public static void save(Zoo zoo) throws IOException {
        if (zoo == null) {
            throw new IllegalArgumentException("Zoo state is required.");
        }
        Files.createDirectories(STATE_FILE.getParent());
        try (ObjectOutputStream output = new ObjectOutputStream(Files.newOutputStream(STATE_FILE))) {
            output.writeObject(zoo);
            output.flush();
        }
    }
}
