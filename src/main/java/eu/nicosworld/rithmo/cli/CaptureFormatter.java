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
            return "car " + p.actorValue() + mapDegree(p.degree()) + " = " + p.targetValue();
        }
        String degree = "";
        if(p.degree() > 2) {
            degree = mapDegree(p.degree());
        }
        return degree + "√" +  p.actorValue() + " = " + p.targetValue();
    }

    private static String mapDegree(int degree) {
        return switch (degree) {
            case 2 -> "²";
            case 3 -> "³";
            case 4 -> "⁴";
            case 5 -> "⁵";
            case 6 -> "⁶";
            case 7 -> "⁷";
            case 8 -> "⁸";
            case 9 -> "⁹";
            default -> throw new RuntimeException("Non supported");
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
