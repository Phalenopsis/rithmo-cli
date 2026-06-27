package eu.nicosworld.rithmo.cli;

import eu.nicosworld.rithmo.core.game.GameStatusDTO;
import eu.nicosworld.rithmo.core.game.dto.decision.DecisionDTO;
import eu.nicosworld.rithmo.engine.model.Position;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class DecisionIndex {

  private final Map<String, UUID> index;

  public DecisionIndex(GameStatusDTO status) {
    this.index =
        status.possibleDecisions().stream()
            .filter(d -> !d.skip())
            .collect(Collectors.toMap(this::key, DecisionDTO::id, (a, b) -> a));
  }

  public UUID find(String actor, Set<String> captured, Position landing) {
    return index.get(buildKey(actor, captured, landing));
  }

  public UUID findSkip() {
    return index.values().stream().findFirst().orElseThrow();
  }

  private String key(DecisionDTO d) {
    return buildKey(d.actorId(), d.capturedIdList(), d.landing());
  }

  private String buildKey(String actor, Set<String> captured, Position landing) {
    return actor
        + "|"
        + (landing != null ? landing.toString() : "null")
        + "|"
        + (captured == null ? "" : captured.stream().sorted().collect(Collectors.joining(",")));
  }
}
