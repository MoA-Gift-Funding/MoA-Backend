package moa;

public class Crons {

    public static final String EVERY_MIDNIGHT = "0 0 0 * * *";
    public static final String EVERY_10_MINUTE_FROM_06_TO_23_HOURS = "0 */10 6-23 * * *";
    public static final String WHEN_12_AND_24_HOURS = "0 0 0,12 * * *";
}
