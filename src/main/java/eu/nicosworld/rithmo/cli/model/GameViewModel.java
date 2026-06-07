package eu.nicosworld.rithmo.cli.model;

import eu.nicosworld.rithmo.core.game.dto.status.PhaseDTO;
import eu.nicosworld.rithmo.core.game.dto.status.PlayerColorDTO;

import java.util.List;

public record GameViewModel(
        BoardView board,
        PlayerColorDTO currentPlayer,
        PhaseDTO phase,
        PyramidDetailsView pyramids,
        AssetsView assets,
        List<UIOption> options
) {}
