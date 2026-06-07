package eu.nicosworld.rithmo.cli.model;

import eu.nicosworld.rithmo.engine.model.Position;

public record PieceView(
        String display,
        Position position
) {}
