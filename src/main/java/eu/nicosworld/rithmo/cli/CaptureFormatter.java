package eu.nicosworld.rithmo.cli;

import eu.nicosworld.rithmo.core.game.dto.option.CaptureOptionDTO;
import eu.nicosworld.rithmo.core.game.dto.option.justification.*;

public class CaptureFormatter {
    public static String formatCapture(CaptureOptionDTO capture) {
        String type = capture.type().name();
        String justification = mapJustification(capture);

        return "\t\t" +type + " : " + justification;
    }

    private static String mapJustification(CaptureOptionDTO capture) {
        return switch (capture.justification()) {
            case EncounterJustificationDTO e -> mapEncounter(e);
            case AmbushJustificationDTO a -> mapAmbush(capture);
            case AssaultJustificationDTO a -> mapAssault(a);
            case PowerJustificationDTO p -> mapPower(p);
        };
    }

    private static String mapPower(PowerJustificationDTO p) {
        if(p.relation().equals(PowerRelationDTO.POWER)) {
            return "car " + p.actorValue() + powerLabel(p.degree()) + " est égal à " + p.targetValue();
        }
        return "car la " + rootLabel(p.degree()) + " de " + p.actorValue() +  " est égal à " + p.targetValue();
    }

    private static String powerLabel(int degree) {
        return switch (degree) {
            case 2 -> "au carré";
            case 3 -> "au cube";
            case 4 -> "à la puissance quatre";
            case 5 -> "à la puissance cinq";
            case 6 -> "à la puissance six";
            case 7 -> "à la puissance sept";
            case 8 -> "à la puissance huit";
            case 9 -> "à la puissance neuf";
            default -> "à la puissance " + degree;
        };
    }

    private static String rootLabel(int degree) {
        return switch (degree) {
            case 2 -> "racine carrée";
            case 3 -> "racine cubique";
            case 4 -> "racine quatrième";
            case 5 -> "racine cinquième";
            case 6 -> "racine sixième";
            case 7 -> "racine septième";
            case 8 -> "racine huitième";
            case 9 -> "racine neuvième";
            default -> "racine " + degree + "ième";
        };
    }

    private static String mapAssault(AssaultJustificationDTO a) {
        return "il y a " + a.distance() + " case(s) entre l'assaillant et la cible et " + a.distance() + mapAssaultOperator(a.operator()) + a.actorValue() + " = " + a.targetValue();
    }

    private static String mapEncounter(EncounterJustificationDTO justification) {
        return "l'attaquant a la même valeur que la cible.";
    }

    private static String mapAmbush(CaptureOptionDTO option) {
        AmbushJustificationDTO justification = (AmbushJustificationDTO) option.justification();
        Formatter formatter = new Formatter();
        String actor;
        String ally;
        String allyDetails = allyDetails = "(" + formatter.formatActor(option.ally().getFirst()) + ")";


        if (justification.operandsReversed()) {
            actor = String.valueOf(justification.supporterValue());
            ally = String.valueOf(justification.actorValue());
        } else {
            actor = String.valueOf(justification.actorValue());
            ally = String.valueOf(justification.supporterValue());
        }
        return "avec l'allié " + allyDetails + " : "
            + actor
            + mapAmbushOperator(justification.operator())
            + ally
            + " = "
            + justification.targetValue();
    }

    private static String mapAmbushOperator(AmbushOperatorDTO operator) {
        return switch (operator) {
            case DIVIDE -> " / ";
            case SUBTRACT -> " - ";
            case MULTIPLY -> " * ";
            case ADD -> " + ";
        };
    }

    private static String mapAssaultOperator(AssaultOperatorDTO operator) {
        return switch (operator) {
            case MULTIPLY -> " * ";
            case DIVIDE -> " / ";
        };
    }
}
