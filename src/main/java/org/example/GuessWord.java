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
    private int step;

    public GuessWord(String filePath) {
        words = new ArrayList<>();
        presentLettersSet = new HashSet<>();
        absentLettersSet = new HashSet<>();
        pattern = "*****";
        lastPattern = pattern;
        loadWords(filePath);
        step = 0;  // Инициализация шага
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
        input = input.trim().toLowerCase();

        switch (step) {
            case 0:
                step++;
                return "Введите буквы, которые ИЗВЕСТНЫ через запятую, либо введите 0.";

            case 1:
                if (!input.equals("0")) {
                    for (String letter : input.split(",")) {
                        presentLettersSet.add(letter.trim());
                    }
                }
                step++;
                return "Введите буквы, которые ОТСУТСТВУЮТ, через запятую, либо введите 0.";

            case 2:
                if (!input.equals("0")) {
                    for (String letter : input.split(",")) {
                        absentLettersSet.add(letter.trim());
                    }
                }
                step++;
                return "Введите шаблон по примеру \"*о*о*\", либо введите 0.";

            case 3:
                if (!input.equals("0")) {
                    if (input.length() == 5) {
                        lastPattern = input;
                        pattern = input;
                    } else {
                        return "Ошибка: шаблон должен содержать ровно 5 символов.";
                    }
                }

                List<String> possibleWords = words.stream()
                        .filter(word -> presentLettersSet.stream().allMatch(word::contains))
                        .filter(word -> absentLettersSet.stream().noneMatch(word::contains))
                        .filter(this::matchesPattern)
                        .toList();

                step = 0;  // Сброс шага для новой игры

                if (possibleWords.isEmpty()) {
                    return "Нет подходящих слов.";
                } else {
                    return "Возможные слова:\n" + String.join("\n", possibleWords);
                }

            default:
                return "Произошла ошибка. Попробуйте снова.";
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
        step = 0;
    }
}
