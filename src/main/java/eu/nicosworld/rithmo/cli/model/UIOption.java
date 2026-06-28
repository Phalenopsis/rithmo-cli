package eu.nicosworld.rithmo.cli.model;

import java.util.UUID;

/**
 * @param decisionId
 * @param label
 * @param type
 */
public record UIOption(UUID decisionId, String label, OptionType type) {}
