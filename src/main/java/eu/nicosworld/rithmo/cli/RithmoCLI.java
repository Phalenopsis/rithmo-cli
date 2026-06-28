package eu.nicosworld.rithmo.cli;

import eu.nicosworld.rithmo.cli.model.BoardView;
import eu.nicosworld.rithmo.cli.model.GameViewModel;
import eu.nicosworld.rithmo.cli.model.PieceView;
import eu.nicosworld.rithmo.cli.persistence.InMemoryGameRepository;
import eu.nicosworld.rithmo.cli.persistence.InMemoryOptionRepository;
import eu.nicosworld.rithmo.core.GameFacade;
import eu.nicosworld.rithmo.core.PreDefinedGame;
import eu.nicosworld.rithmo.core.exception.PatException;
import eu.nicosworld.rithmo.core.exception.VictoryException;
import eu.nicosworld.rithmo.core.game.Game;
import eu.nicosworld.rithmo.core.game.GameStatusDTO;
import java.util.UUID;

public class RithmoCLI {

  private final GameFacade facade;
  private final ConsoleInputHandler inputHandler;

  private final GameViewMapper mapper;

  private GameStatusDTO status;

  public RithmoCLI() {

    InMemoryOptionRepository optionRepository = new InMemoryOptionRepository();
    InMemoryGameRepository gameRepository = new InMemoryGameRepository();

    this.facade = new GameFacade(gameRepository, optionRepository);
    this.inputHandler = new ConsoleInputHandler();

    this.mapper = new GameViewMapper();
  }

  public void run() {

    try {
      Game game = PreDefinedGame.fourEightBoardGame();
      // Game game = PreDefinedTestGame.gameWithMultiCaptures_FourRules();
      status = facade.startGame(game);

      while (true) {

        // 1. MAP DTO → VIEW
        var view = mapper.toView(status);

        // 2. RENDER
        render(view);

        // 3. USER INPUT
        UUID actionId = prompt(view);

        // 4. APPLY ACTION
        status = facade.play(status.gameId(), actionId);
      }

    } catch (VictoryException e) {
      handleVictory(e);

    } catch (PatException e) {
      handlePat(e);
    }
  }

  // =====================================================
  // PROMPT
  // =====================================================

  private UUID prompt(GameViewModel view) {

    var options = view.options();

    if (options.isEmpty()) {
      throw new IllegalStateException("No available options");
    }

    System.out.println("\n=== OPTIONS ===");

    for (int i = 0; i < options.size(); i++) {
      System.out.println("[" + (i + 1) + "] " + options.get(i).label());
    }

    int index = inputHandler.readInt("Choisissez une action", 1, options.size()) - 1;

    return options.get(index).decisionId();
  }

  // =====================================================
  // RENDER
  // =====================================================

  private void render(GameViewModel view) {

    BoardView board = view.board();

    String whitePyramid = "";
    String blackPyramid = "";

    String[][] grid = new String[board.height()][board.width()];

    for (int y = 0; y < board.height(); y++) {
      for (int x = 0; x < board.width(); x++) {
        grid[y][x] = "  .  ";
      }
    }

    for (PieceView piece : board.pieces()) {

      int x = piece.position().getX();
      int y = piece.position().getY();

      grid[y][x] = String.format("%-5s", piece.display());
    }

    System.out.println("\n--- RITHMOMACHIA ---");

    System.out.print("    ");
    for (int x = 0; x < board.width(); x++) {
      System.out.printf("   %c   ", x + 'A');
    }
    System.out.println();

    for (int y = 0; y < board.height(); y++) {

      System.out.printf("%-4d", y + 1);

      for (int x = 0; x < board.width(); x++) {
        System.out.print("[" + grid[y][x] + "]");
      }

      System.out.println();
    }

    System.out.println("--------------------------------");
    System.out.println("Player : " + view.currentPlayer());
    System.out.println("Phase  : " + view.phase());
    System.out.println();
    System.out.println("PYRAMIDS");
    System.out.println("WHITE : " + view.pyramids().whitePyramid());
    System.out.println("BLACK : " + view.pyramids().blackPyramid());
    System.out.println("ASSETS");
    System.out.println("WHITE");
    System.out.println("Captured : " + view.assets().white().captured());
    System.out.println("Reserve  : " + view.assets().white().reserve());
    System.out.println();
    System.out.println("BLACK");
    System.out.println("Captured : " + view.assets().black().captured());
    System.out.println("Reserve  : " + view.assets().black().reserve());
  }

  // =====================================================
  // ENDGAME
  // =====================================================

  private void handleVictory(VictoryException e) {
    VictoryFormatter formatter = new VictoryFormatter(e.getVictoryDto());
    System.out.println(formatter.format());
  }

  private void handlePat(PatException e) {
    System.out.println("\nPAT: " + e.getMessage());
  }
}
