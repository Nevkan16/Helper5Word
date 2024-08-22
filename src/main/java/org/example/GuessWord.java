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
    private int step;

    public GuessWord(String filePath) {
        words = new ArrayList<>();
        presentLettersSet = new HashSet<>();
        absentLettersSet = new HashSet<>();
        pattern = "*****";
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

    public String processInput(String input) {
        input = input.trim().toLowerCase();
        StringBuilder response = new StringBuilder();

        while (true) {
            switch (step) {
                case 0 -> {
                    step++;
                    return response.append("\n\nВведите буквы, которые ИЗВЕСТНЫ через запятую, либо введите 0.").toString();
                }
                case 1 -> {
                    if (!input.equals("0")) {
                        for (String letter : input.split(",")) {
                            presentLettersSet.add(letter.trim());
                        }
                    }
                    step++;
                    return response.append("Введите буквы, которые ОТСУТСТВУЮТ, через запятую, либо введите 0.").toString();
                }
                case 2 -> {
                    if (!input.equals("0")) {
                        for (String letter : input.split(",")) {
                            absentLettersSet.add(letter.trim());
                        }
                    }
                    step++;
                    return response.append("Введите шаблон по примеру \"*о*о*\", либо введите 0.").toString();
                }
                case 3 -> {
                    if (!input.equals("0")) {
                        if (input.length() == 5) {
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
                        response.append("Нет подходящих слов.");
                    } else {
                        response.append("Возможные слова:\n").append(String.join("\n", possibleWords));
                    }

                    // Автоматический переход к началу нового цикла
                    input = "";  // Пустой ввод для перехода к следующему шагу
                }
                default -> {
                    return response.append("Произошла ошибка. Попробуйте снова.").toString();
                }
            }
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
        step = 0;
    }
}
