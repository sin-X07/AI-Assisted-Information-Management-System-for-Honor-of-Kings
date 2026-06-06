package util;

import java.util.Scanner;

public class InputHelper {
    private final Scanner scanner;

    public InputHelper() {
        this.scanner = new Scanner(System.in);
    }

    public String readLine(String prompt) {
        System.out.print(prompt);
        if (!scanner.hasNextLine()) {
            System.out.println();
            System.out.println("输入流已结束，程序退出。");
            System.exit(0);
        }
        return scanner.nextLine().trim();
    }

    public String readRequiredString(String prompt) {
        while (true) {
            String value = readLine(prompt);
            if (!value.isEmpty()) {
                return value;
            }
            System.out.println("输入不能为空，请重新输入。");
        }
    }

    public int readInt(String prompt) {
        while (true) {
            String value = readRequiredString(prompt);
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                System.out.println("请输入有效的整数。");
            }
        }
    }

    public int readIntInRange(String prompt, int min, int max) {
        while (true) {
            int value = readInt(prompt);
            if (value >= min && value <= max) {
                return value;
            }
            System.out.println("请输入 " + min + " 到 " + max + " 之间的数字。");
        }
    }
}
