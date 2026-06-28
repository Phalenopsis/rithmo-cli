package eu.nicosworld.rithmo.cli;

import java.util.Scanner;

public class ConsoleInputHandler {
  private final Scanner scanner = new Scanner(System.in);

  public int readInt(String message, int min, int max) {
    while (true) {
      System.out.print(message + " (" + min + "-" + max + "): ");
      String input = scanner.nextLine();
      try {
        int choice = Integer.parseInt(input);
        if (choice >= min && choice <= max) {
          return choice;
        }
        System.out.println("⚠️ Choix hors limites.");
      } catch (NumberFormatException e) {
        System.out.println("⚠️ Veuillez entrer un nombre valide.");
      }
    }
  }
}
