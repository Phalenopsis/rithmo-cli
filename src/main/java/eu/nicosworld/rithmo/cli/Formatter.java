package eu.nicosworld.rithmo.cli;

import eu.nicosworld.rithmo.core.game.GameStatusDTO;
import eu.nicosworld.rithmo.core.game.dto.board.PieceDTO;
import eu.nicosworld.rithmo.core.game.dto.board.PieceShape;
import eu.nicosworld.rithmo.core.game.dto.status.PlayerColorDTO;
import eu.nicosworld.rithmo.engine.model.Position;


import java.util.HashMap;
import java.util.Map;

public class Formatter {

    private final Map<Position, PieceDTO> pieceMap = new HashMap<>();

    public Formatter(GameStatusDTO gameStatusDTO) {
        for(PieceDTO piece : gameStatusDTO.board().pieces()) {
            pieceMap.put(piece.position(), piece);
        }
    }

    public Formatter() {

    }

    public String formatActor(PieceDTO pieceDTO) {
        if (pieceDTO == null) {
            throw new RuntimeException("In Formatter.formatActor, actor is null");
        }
        Position position = pieceDTO.position();
        // if position is null, it is a piece in reserve
        if (position == null) {
            return formatPiece(pieceDTO);
        }
        PieceDTO parent = pieceMap.get(position);
        if (parent == null || pieceDTO.equals(parent)) {
            return formatPiece(pieceDTO);
        }
        return formatPiece(parent, pieceDTO);
    }

    private String formatPiece(PieceDTO piece, PieceDTO component) {
        String color = (piece.owner() == PlayerColorDTO.WHITE) ? "W" : "B";
        String shape = piece.shape().name().substring(0, 1);

        // Si la pièce est une pyramide et qu'on précise un composant
        if (piece.shape() == PieceShape.PYRAMID && component != null && piece != component) {
            String compShape = component.shape().name().substring(0, 1);
            return String.format("%s%s%d(%s%d)", color, shape, piece.value(), compShape, component.value());
        }

        // Affichage standard pour un pion seul
        return color + shape + piece.value();
    }

    // Surcharge pour les cas simples (compatibilité)
    private String formatPiece(PieceDTO p) {
        return formatPiece(p, null);
    }

    public String formatPyramid(PieceDTO pieceDTO) {
        if(!pieceDTO.shape().equals(PieceShape.PYRAMID)) {
            throw new RuntimeException(pieceDTO + " is not a pyramid");
        }
        StringBuilder sb = new StringBuilder(
                toShortRepresentation(pieceDTO)
        );

        sb.append(" :");

        for (PieceDTO component : pieceDTO.components()) {

            sb.append(" ");
            sb.append(
                    toShortRepresentation(component)
            );
        }

        return sb.toString();
    }

    public String toShortRepresentation(PieceDTO piece) {

        if (piece == null) {
            return "null";
        }

        String owner =
                piece.owner() == null
                        ? "?"
                        : piece.owner()
                        .name()
                        .substring(0, 1);

        String shape =
                shapeCode(piece);

        String value =
                String.valueOf(piece.value());

        return owner
                + shape
                + value;
    }

    private static String shapeCode(PieceDTO piece) {

        if (piece == null || piece.shape() == null) {
            return "?";
        }

        return switch (piece.shape()) {
            case CIRCLE -> "C";
            case SQUARE -> "S";
            case TRIANGLE -> "T";
            case PYRAMID -> "P";
        };
    }
}
