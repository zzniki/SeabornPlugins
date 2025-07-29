package com.oheers.fish.placeholders;

public enum Priority {
    /**
     * For exact string matches like %emf_competition_type%
     * (Highest priority - checked first)
     */
    EXACT_MATCH,

    /**
     * For specific patterns with arguments like %emf_competition_place_player_1%
     */
    SPECIFIC_PATTERN,

    /**
     * For general patterns like %emf_total_money_earned_<uuid>%
     */
    GENERAL_PATTERN,

    /**
     * Fallback handlers (Lowest priority - checked last)
     */
    FALLBACK
}