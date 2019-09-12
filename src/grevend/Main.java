/*
 * MIT License
 *
 * Copyright (c) 2019 David Greven
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package grevend;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    private static String currentJdkVersion = "1.8";

    public static void main(String[] args) {
        CommandLineInterface.init(args, (action -> {
            if (action == Action.UPDATE) {
                changeProperties();
                if (CommandLineInterface.showRecommendation(currentJdkVersion) == Action.CHANGEJDK) {
                    changeJDK();
                }
            } else if (action == Action.RESTORE) {
                restoreBackup();
            }
        }));
    }

    private static Stream<Path> findFiles(Path dir) throws IOException {
        return Files.walk(dir).filter(Files::isRegularFile).filter(path -> path.getFileName().endsWith("project.properties"));
    }

    private static void processFiles(ConsumerWithIOException<Path> consumer, String... output) {
        try {
            System.out.println();
            findFiles(Paths.get(System.getProperty("user.dir"))).forEach(file -> {
                try {
                    if (CommandLineInterface.verboseOutput) {
                        System.out.println(output[0] + " " + file + "...");
                    }
                    consumer.accept(file);
                } catch (IOException e) {
                    CommandLineInterface.reportIssue();
                    e.printStackTrace();
                }
            });
            if (!CommandLineInterface.verboseOutput) {
                System.out.println(output[1]);
            }
        } catch (IOException e) {
            CommandLineInterface.reportIssue();
            e.printStackTrace();
        }
    }

    private static void changeProperties() {
        processFiles((file) -> {
            Files.copy(file, Paths.get(file.getParent() + File.separator + "project-backup.properties"), StandardCopyOption.REPLACE_EXISTING);
            Stream<String> lines = Files.lines(file);
            List<String> replacements = lines.map(Main::replaceSourceAndTarget).collect(Collectors.toList());
            Files.write(file, replacements);
            lines.close();
        }, "Processing", "Created backups\nUpdated properties");
    }

    private static void changeJDK() {
        processFiles((file) -> {
            Stream<String> lines = Files.lines(file);
            List<String> replacements = lines.map(Main::replaceJDK).collect(Collectors.toList());
            Files.write(file, replacements);
            lines.close();
        }, "Processing", "Changed JDKs");
    }

    private static void restoreBackup() {
        processFiles(Main::restoreProperties, "Restored backup", "Restored backups");
    }

    private static void restoreProperties(Path path) throws IOException {
        Path backupPath = Paths.get(path.getParent() + File.separator + "project-backup.properties");
        if (Files.exists(path) && Files.exists(backupPath)) {
            Files.copy(backupPath, path, StandardCopyOption.REPLACE_EXISTING);
            if (CommandLineInterface.verboseOutput) {
                System.out.println("Restored backup " + path + "...");
            }
        } else {
            if (CommandLineInterface.verboseOutput) {
                System.out.println("No backup found for " + path);
            }
        }
    }

    private static String replaceSourceAndTarget(String line) {
        if (line.startsWith("javac.source")) {
            return "javac.source=1.8";
        } else if (line.startsWith("javac.target")) {
            return "javac.target=1.8";
        } else if (line.startsWith("platform.active")) {
            if (!line.replace("platform.active=", "").equals("JDK_1.8")) {
                currentJdkVersion = line.replace("platform.active=", "").replace("JDK_", "");
            }
        }
        return line;
    }

    private static String replaceJDK(String line) {
        if (line.startsWith("platform.active")) {
            return "platform.active=JDK_1.8";
        }
        return line;
    }

}
