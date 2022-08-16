package io.github.techstreet.dfscript.util.hypercube;

public class HypercubeUtil {
    private static HypercubeRank rank = HypercubeRank.DEFAULT;

    public static HypercubeRank getRank() {
        return rank;
    }

    public static void setRank(HypercubeRank rank) {
        if (!(HypercubeUtil.rank.ordinal() >= rank.ordinal())) {
            HypercubeUtil.rank = rank;
        }
    }
}
