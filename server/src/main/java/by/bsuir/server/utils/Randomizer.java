package by.bsuir.server.utils;

import java.util.Random;

public class Randomizer {
    private final Random random;

    public Randomizer() {
        this.random = new Random();
    }

    public int getRandomInt(int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }

    public double getRandomDouble() {
        return random.nextDouble();
    }

    public String getRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            result.append(characters.charAt(index));
        }

        return result.toString();
    }

    public <T> T getRandomElement(T[] array) {
        int index = random.nextInt(array.length);
        return array[index];
    }

    public boolean getRandomBoolean() {
        return random.nextBoolean();
    }
}
