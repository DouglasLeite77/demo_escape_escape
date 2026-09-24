package com.example.demo_escape_escape.entity;

public enum GuardType {

    PATRULHEIRO(
            "Patrulheiro",
            80,
            160,
            1.0,
            1.5,
            2.0
    ),

    INVESTIGADOR(
            "Investigador",
            75,
            150,
            1.8,
            3.0,
            4.0
    ),

    SEGURANCA(
            "Segurança",
            65,
            190,
            0.8,
            1.2,
            2.5
    );

    private final String displayName;
    private final double speed;
    private final double visionRange;
    private final double hearingMultiplier;
    private final double investigateDuration;
    private final double searchDuration;

    GuardType(
            String displayName,
            double speed,
            double visionRange,
            double hearingMultiplier,
            double investigateDuration,
            double searchDuration
    ) {

        this.displayName = displayName;
        this.speed = speed;
        this.visionRange = visionRange;
        this.hearingMultiplier = hearingMultiplier;
        this.investigateDuration = investigateDuration;
        this.searchDuration = searchDuration;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getSpeed() {
        return speed;
    }

    public double getVisionRange() {
        return visionRange;
    }

    public double getHearingMultiplier() {
        return hearingMultiplier;
    }

    public double getInvestigateDuration() {
        return investigateDuration;
    }

    public double getSearchDuration() {
        return searchDuration;
    }
}