package eu.nicosworld.rithmo.cli;

import eu.nicosworld.rithmo.core.game.dto.victory.*;
import java.util.List;

public class VictoryFormatter {
  private final VictoryDTO victoryDTO;

  public VictoryFormatter(VictoryDTO victoryDTO) {
    this.victoryDTO = victoryDTO;
  }

  public String format() {
    return formatWinner() + "\n\n" + formatConditions() + "\n\n" + formatJustifications();
  }

  private String formatJustifications() {
    return "Raisons de la victoire :\n"
        + String.join(
            "\n", victoryDTO.justifications().stream().map(this::formatJustification).toList());
  }

  private String formatJustification(VictoryJustificationDTO justification) {
    return switch (justification) {
      case BodyVictoryDTO b -> formatBodyJustification(b);
      case GoodsVictoryDTO g -> formatGoodsJustification(g);
      case LawsuitVictoryDTO l -> formatLawsuitJustification(l);
    };
  }

  private String formatBodyJustification(BodyVictoryDTO justification) {
    return "\tNombre de pièces capturées/requises : "
        + justification.actual()
        + "/"
        + justification.required();
  }

  private String formatGoodsJustification(GoodsVictoryDTO justification) {
    return "\tValeur totale capturée/requise : "
        + justification.actual()
        + "/"
        + justification.required();
  }

  private String formatLawsuitJustification(LawsuitVictoryDTO justification) {
    return "\tNombre de chiffres capturés/requis : "
        + justification.actual()
        + "/"
        + justification.required();
  }

  private String formatWinner() {
    return "Et le gagnant est " + victoryDTO.winner().name() + " !";
  }

  private String formatConditions() {
    List<String> conditions =
        victoryDTO.conditions().stream().map(this::mapVictoryCondition).toList();

    return "Conditions de victoire remplies :\n\t- " + String.join("\n\t- ", conditions);
  }

  private String mapVictoryCondition(VictoryConditionDTO dto) {
    return switch (dto) {
      case BODY -> "de corps";
      case GOODS -> "de bien";
      case LAWSUIT -> "de procès";
      case BODY_AND_GOODS -> "de corps et de bien";
      case BODY_AND_LAWSUIT -> "de corps et de procès";
      case GOODS_AND_LAWSUIT -> "de bien et de procès";
      case BODY_AND_GOODS_AND_LAWSUIT -> "de corps, de bien et de procès";
    };
  }
}
