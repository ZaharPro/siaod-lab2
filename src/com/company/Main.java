package com.company;

import java.io.*;
import java.util.Scanner;
import java.util.function.Consumer;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final HashMap<String, String> map = new HashMap<>(2);

    public static void main(String[] args) {
        map.put("12", "asdf");
        map.put("21", "asjhldf");
        map.put("tryeuwio", "ldfjasdf");
        map.put("1234567", "-0987654");
        map.put("pqweoirupqw", "alksdf;alsdj");
        System.out.println(map.toString());
        String message = "Enter operation :\n" +
                "Add = 1\n" +
                "Get = 2\n" +
                "Remove = 3\n" +
                "Contains key = 4\n" +
                "Contains value = 5\n" +
                "Print map = 6\n" +
                "Exit = 7";
        boolean inLoop = true;
        do {
            System.out.println(message);
            switch (scanner.nextLine()) {
                case "1" -> put();
                case "2" -> get();
                case "3" -> remove();
                case "4" -> containsKey();
                case "5" -> containsValue();
                case "6" -> printMap();
                case "7" -> inLoop = false;
                default -> System.out.println("Line is not correct");
            }
        } while (inLoop);
    }

    private static void put() {
        System.out.println("Enter key");
        String key = scanner.nextLine();
        System.out.println("Enter value");
        String value = scanner.nextLine();

        String oldValue = map.put(key, value);
        System.out.println("Saved");
        if (oldValue != null)
            System.out.println("Old value is: \"" + oldValue + "\"");
    }
    private static void get() {
        System.out.println("Enter key");
        String key = scanner.nextLine();
        System.out.println(map.get(key));
    }
    private static void remove() {
        System.out.println("Enter key");
        String key = scanner.nextLine();
        String oldValue = map.remove(key);
        if (oldValue == null) {
            System.out.println("Key already deleted");
        } else {
            System.out.println("Removed\nOld value is: \"" + oldValue + "\"");
        }
    }
    private static void containsKey() {
        System.out.println("Enter key");
        String key1 = scanner.nextLine();
        System.out.println(map.containsKey(key1) ? "Yes" : "No");
    }
    private static void containsValue() {
        System.out.println("Enter value");
        String value1 = scanner.nextLine();
        System.out.println(map.containsValue(value1) ? "Yes" : "No");
    }
    private static void printMap() {
        String line;
        label:
        while (true) {
            System.out.println("Print to console = 1\n to file = 2\ncancel = 3");
            line = scanner.nextLine();
            switch (line) {
                case "1":
                    forEachEntry(System.out::println);
                    break;
                case "2":
                    System.out.println("Enter path to file");
                    String path = scanner.nextLine();
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
                        Consumer<String> consumer = s -> {
                            try {
                                writer.write(s);
                                writer.write('\r');
                                writer.write('\n');
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        };
                        forEachEntry(consumer);
                        System.out.println("Saved");
                    } catch (IOException e) {
                        System.out.println("File not found");
                    }
                    break;
                case "3":
                    break label;
                default:
            }
        }
    }
    private static void forEachEntry(Consumer<String> consumer) {
        map.forEach((key, value) -> consumer.accept("Key = " + key + ", value = " + value));
    }
}