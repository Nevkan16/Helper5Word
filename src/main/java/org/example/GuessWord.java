package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GuessWord {
    private final List<String> words;
    private static Set<String> presentLettersSet;
    private static Set<String> absentLettersSet;
    private String pattern;
    private String lastPattern;

    public GuessWord(String filePath) {
        words = new ArrayList<>();
        presentLettersSet = new HashSet<>();
        absentLettersSet = new HashSet<>();
        pattern = "*****";
        lastPattern = pattern;
        loadWords(filePath);
    }

    // Метод для загрузки слов из файла
    private void loadWords(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                words.add(line.trim().toLowerCase());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Метод для обработки ввода пользователя
    public String processInput(String input) {
        String[] commands = input.split(":");
        if (commands.length > 1) {
            String commandType = commands[0].trim();
            String commandData = commands[1].trim();

            if (commandType.equalsIgnoreCase("present")) {
                for (String letter : commandData.split(",")) {
                    presentLettersSet.add(letter.trim());
                }
            } else if (commandType.equalsIgnoreCase("absent")) {
                for (String letter : commandData.split(",")) {
                    absentLettersSet.add(letter.trim());
                }
            } else if (commandType.equalsIgnoreCase("pattern")) {
                if (commandData.length() == 5) {
                    lastPattern = commandData;
                    pattern = commandData;
                } else {
                    return "Ошибка: шаблон должен содержать ровно 5 символов.";
                }
            } else if (commandType.equalsIgnoreCase("reset")) {
                reset();
                return "Данные сброшены.";
            } else {
                return "Неизвестная команда.";
            }

            List<String> possibleWords = words.stream()
                    .filter(word -> presentLettersSet.stream().allMatch(word::contains))
                    .filter(word -> absentLettersSet.stream().noneMatch(word::contains))
                    .filter(this::matchesPattern)
                    .toList();

            if (possibleWords.isEmpty()) {
                return "Нет подходящих слов.";
            } else {
                return "Возможные слова:\n" + String.join("\n", possibleWords);
            }
        } else {
            return "Неверный формат ввода. Используйте: present:а,б; absent:в,г; pattern:*о*о*";
        }
    }

    // Метод для проверки соответствия слова шаблону
    private boolean matchesPattern(String word) {
        for (int i = 0; i < pattern.length(); i++) {
            char patternChar = pattern.charAt(i);
            if (patternChar != '*' && word.charAt(i) != patternChar) {
                return false;
            }
        }
        return true;
    }

    // Метод для сброса данных
    public void reset() {
        presentLettersSet.clear();
        absentLettersSet.clear();
        pattern = "*****";
        lastPattern = pattern;
    }
}
