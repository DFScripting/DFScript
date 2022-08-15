package io.github.techstreet.dfscript.util.hypercube;

public enum HypercubeRank {

    DEFAULT(null),

    JRHELPER("JR_HELPER"),
    HELPER("HELPER"),
    SRHELPER("SR_HELPER"),
    JRMOD("JR_MOD"),
    MOD("MOD"),
    SRMOD("SR_MOD"),
    DEV("DEV"),
    ADMIN("ADMIN"),
    OWNER("OWNER"),
    ;

    private String teamName;

    HypercubeRank(String teamName) {
        this.teamName = teamName;
    }

    public String getTeamName() {
        return teamName;
    }

    public boolean hasPermission(HypercubeRank req) {
        return this.ordinal() >= req.ordinal();
    }
}
