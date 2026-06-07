package eu.nicosworld.rithmo.cli.model;

import java.util.List;

public record BoardView(
        int width,
        int height,
        List<PieceView> pieces
) {}
