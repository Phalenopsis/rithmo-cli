package eu.nicosworld.rithmo.cli;

import eu.nicosworld.rithmo.cli.model.*;
import eu.nicosworld.rithmo.core.game.GameStatusDTO;
import eu.nicosworld.rithmo.core.game.dto.board.PieceDTO;
import eu.nicosworld.rithmo.core.game.dto.board.PieceShape;
import eu.nicosworld.rithmo.core.game.dto.board.PlayerAssetsDTO;
import eu.nicosworld.rithmo.core.game.dto.decision.DecisionDTO;
import eu.nicosworld.rithmo.core.game.dto.option.*;
import eu.nicosworld.rithmo.core.game.dto.status.PlayerColorDTO;
import eu.nicosworld.rithmo.core.helper.JustificationStringMapper;
import eu.nicosworld.rithmo.engine.model.Position;

import java.util.*;

public class GameViewMapper {

    public GameViewModel toView(GameStatusDTO status) {
        Formatter formatter = new Formatter(status);
        DecisionIndex decisionIndex = new DecisionIndex(status);

        return new GameViewModel(
                mapBoard(status, formatter),
                status.currentPlayer(),
                status.phase(),
                mapPyramids(status, formatter),
                mapAssets(status, formatter),
                mapOptions(status, formatter)
        );
    }

    private BoardView mapBoard(GameStatusDTO status, Formatter formatter) {

        return new BoardView(
                status.board().width(),
                status.board().height(),
                status.board().pieces().stream()
                        .map(p -> new PieceView(
                                formatter.formatActor(p),
                                p.position()
                        ))
                        .toList()
        );
    }



    private Map<String, PieceDTO> mapActor(GameStatusDTO status) {
        Map<String, PieceDTO> map = new HashMap<>();
        for(PieceDTO pieceDTO: status.board().pieces()) {
            map.put(pieceDTO.id(), pieceDTO);
            if(pieceDTO.shape().equals(PieceShape.PYRAMID)) {
                for (PieceDTO componentDTO: pieceDTO.components()) {
                    map.put(componentDTO.id(), componentDTO);
                }
            }
        }
        for(PieceDTO pieceDTO: status.assets().get(status.currentPlayer()).reserve()) {
            map.put(pieceDTO.id(), pieceDTO);
        }
        return map;
    }

    private List<UIOption> mapOptions(GameStatusDTO status, Formatter formatter) {

        Map<String, PieceDTO> actors = mapActor(status);

        List<UIOption> uiOptions = new ArrayList<>();

//        System.out.println("** PRINT POSSIBLES OPTIONS **");
//        for (Map.Entry<PieceDTO, Set<PlayerOptionDTO>> entry : status.possibleOptions().entrySet()) {
//            PieceDTO piece = entry.getKey();
//
//            for (PlayerOptionDTO option : entry.getValue()) {
//                System.out.println(piece + " : " + option);
//            }
//        }
//
//        System.out.println("**** END PRINT POSSIBLES OPTIONS ****");

        for (DecisionDTO decision : status.possibleDecisions()) {
 //           System.out.println(decision);

            // ============================================
            // SKIP
            // ============================================

            if (decision.skip()) {

                uiOptions.add(
                        new UIOption(
                                decision.id(),
                                "PASS / END TURN",
                                OptionType.SKIP
                        )
                );

                continue;
            }

            PieceDTO actor = actors.get(decision.actorId());

            // ============================================
            // MOVE / REINTRODUCTION
            // ============================================

            if (decision.capturedIdList() == null
                    || decision.capturedIdList().isEmpty()) {

                // ========================================
                // MOVE
                // ========================================

                if (actor.position() != null) {

                    uiOptions.add(
                            new UIOption(
                                    decision.id(),
                                    buildMoveDecisionLabel(
                                            status,
                                            formatter,
                                            actor,
                                            decision
                                    ),
                                    OptionType.MOVE
                            )
                    );
                }

                // ========================================
                // REINTRODUCTION
                // ========================================

                else {

                    uiOptions.add(
                            new UIOption(
                                    decision.id(),
                                    buildReintroductionDecisionLabel(
                                            formatter,
                                            actor,
                                            decision
                                    ),
                                    OptionType.REINTRODUCTION
                            )
                    );
                }

                continue;
            }

            // ============================================
            // CAPTURE
            // ============================================

            List<PlayerOptionDTO> matchingOptions =
                    findMatchingCaptureOptions(
                            status,
                            decision
                    );

            boolean isPreCapture =
                    matchingOptions.stream()
                            .anyMatch(PreCaptureOptionDTO.class::isInstance);

            uiOptions.add(
                    new UIOption(
                            decision.id(),
                            buildCaptureDecisionLabel(
                                    formatter,
                                    actor,
                                    decision,
                                    matchingOptions,
                                    isPreCapture
                            ),
                            isPreCapture
                                    ? OptionType.PRE_CAPTURE
                                    : OptionType.POST_CAPTURE
                    )
            );
        }

        return uiOptions;
    }

    private String buildMoveDecisionLabel(
            GameStatusDTO status,
            Formatter formatter,
            PieceDTO actor,
            DecisionDTO decision
    ) {

        MoveOptionDTO moveOption =
                findMoveOption(status, actor, decision.landing());

        return String.format(
                "%s : MOVE %s -> %s (%s)",
                formatter.formatActor(actor),
                toChessCoordinates(actor.position()),
                toChessCoordinates(decision.landing()),
                moveOption.typeDTO()
        );
    }

    private List<PlayerOptionDTO> findMatchingCaptureOptions(
            GameStatusDTO status,
            DecisionDTO decision
    ) {

        Set<String> capturedIds =
                new HashSet<>(decision.capturedIdList());

        return status.possibleOptions()
                .values()
                .stream()
                .flatMap(Set::stream)

                .filter(option ->
                    option instanceof CaptureOptionDTO
                )

                .filter(option -> {

                    PieceDTO target;

                    if (option instanceof PreCaptureOptionDTO pre) {
                        target = pre.target();
                        return capturedIds.contains(target.id());
                    }

                    if (option instanceof CaptureOptionDTO post) {
                        target = post.target();
                        return capturedIds.contains(target.id());
                    }

                    return false;
                })

                .toList();
    }

    private String buildReintroductionDecisionLabel(
            Formatter formatter,
            PieceDTO actor,
            DecisionDTO decision
    ) {

        return String.format(
                "REINTRODUCE %s -> %s",
                formatter.formatActor(actor),
                toChessCoordinates(decision.landing())
        );
    }

    private String buildCaptureDecisionLabel(
            Formatter formatter,
            PieceDTO actor,
            DecisionDTO decision,
            List<PlayerOptionDTO> options,
            boolean preCapture
    ) {

        String phase =
                preCapture
                        ? "PRE-CAPTURE"
                        : "POST-CAPTURE";

        String actorLabel =
                formatter.formatActor(actor);

        Set<String> targets =
                new HashSet<>();

        Set<String> types =
                new LinkedHashSet<>();

        for (PlayerOptionDTO option : options) {

            if (option instanceof CaptureOptionDTO capture) {

                targets.add(
                        formatter.formatActor(capture.target())
                );

                types.add(CaptureFormatter.formatCapture(capture));
            }
        }

        String targetLabel =
                String.join(" + ", targets);

        String typeLabel =
                String.join("\n", types);

        String landing =
                decision.landing() != null
                        ? " -> " + toChessCoordinates(decision.landing())
                        : "";

        return String.format(
                "%s: %s ⚔️ %s%s \n%s",
                phase,
                actorLabel,
                targetLabel,
                landing,
                typeLabel
        );
    }

    private MoveOptionDTO findMoveOption(
            GameStatusDTO status,
            PieceDTO actor,
            Position landing
    ) {

        return status.possibleOptions()
                .getOrDefault(actor, Set.of())
                .stream()
                .filter(MoveOptionDTO.class::isInstance)
                .map(MoveOptionDTO.class::cast)
                .filter(move ->
                        Objects.equals(move.to(), landing)
                )
                .findFirst()
                .orElseThrow(() ->
                        new IllegalStateException(
                                "No matching move option found for landing "
                                        + landing
                        )
                );
    }

    private PyramidDetailsView mapPyramids(
            GameStatusDTO status,
            Formatter formatter
    ) {

        String white = "";
        String black = "";

        for (PieceDTO piece : status.board().pieces()) {

            if (piece.shape() != PieceShape.PYRAMID) {
                continue;
            }

            String formatted =
                    formatter.formatPyramid(piece);

            if (piece.owner() == PlayerColorDTO.WHITE) {
                white = formatted;
            }
            else {
                black = formatted;
            }
        }

        return new PyramidDetailsView(
                white,
                black
        );
    }

    private AssetsView mapAssets(GameStatusDTO status, Formatter formatter) {

        PlayerAssetsDTO white = status.assets().get(PlayerColorDTO.WHITE);
        PlayerAssetsDTO black = status.assets().get(PlayerColorDTO.BLACK);

        return new AssetsView(
                new PlayerAssetsView(
                        formatList(white.captured(), formatter),
                        formatList(white.reserve(), formatter)
                ),
                new PlayerAssetsView(
                        formatList(black.captured(), formatter),
                        formatList(black.reserve(), formatter)
                )
        );
    }

    private String formatList(List<PieceDTO> pieces, Formatter formatter) {

        if (pieces == null || pieces.isEmpty()) {
            return "";
        }

        return pieces.stream()
                .map(formatter::toShortRepresentation)
                .reduce((a, b) -> a + " " + b)
                .orElse("");
    }

    public static String toChessCoordinates(Position position) {
        char letter = (char) ('A' + position.getX());
        int number = position.getY() + 1;
        return "" + letter + number;
    }
}
