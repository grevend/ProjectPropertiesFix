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

import java.util.Scanner;

public class Menu {

    private static Scanner reader = new Scanner(System.in);

    public static Action showMenu() {
        System.out.println("\nProjectPropertiesFix - 1.0 (Beta) - David Greven\n\n" +
                "This application upgrades all projects target and source properties in your current directory to Java 8.\n" +
                "Please ensure that you have installed JDK 8 or higher before starting the process.\n" +
                "If you find a bug please report it on github https://github.com/grevend/ProjectPropertiesFix/issues.\n\n" +
                "Options:\n1. Create backups and update properties\n2. Restore backups\n"
        );
        System.out.print("Select your option (1/2) to start the process or press enter to close the application: ");
        String action = reader.nextLine().trim().toLowerCase();
        System.out.println();
        if (action.equals("1") || action.equals("1)") || action.equals("1.") || action.equals("a")) {
            return Action.UPDATE;
        } else if (action.equals("2") || action.equals("2)") || action.equals("2.") || action.equals("b")) {
            return Action.RESTORE;
        }
        return Action.NONE;
    }

    public static void reportIssue() {
        System.out.println("Please report your issue with a copy of the stacktrace below to https://github.com/grevend/ProjectPropertiesFix/issues.");
    }

    public static Action showRecommendation(String currentJdkVersion) {
        if (!currentJdkVersion.equals("1.8")) {
            System.out.println("\nJDK versions below or above 8 may lead to incompatibilities with TMC.\n" +
                    "Ensure that your current default JDK version is set to 8 or 1.8.\n" +
                    "Netbeans: Run > Set Project Configuration > Customize... > Libraries > Java Platform\n"
            );
            return confirmAction("If you have JDK version 8 setup in Netbeans do you want to change the " + (currentJdkVersion.equals("default_platform") ? "generic " : "") + "configuration '" + currentJdkVersion + "' to JDK 8?") ? Action.CHANGEJDK : Action.NONE;
        }
        return Action.NONE;
    }

    public static boolean confirmAction(String text) {
        System.out.print(text + " (y/n): ");
        return reader.nextLine().trim().toLowerCase().equals("y");
    }

}
