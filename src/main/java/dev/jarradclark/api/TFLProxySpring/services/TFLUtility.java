package dev.jarradclark.api.TFLProxySpring.services;

public final class TFLUtility {
    private TFLUtility(){}

    public static String shortenDestinationName(String destination) {
        return switch (destination) {
            case "Hammersmith, Bus Station" -> "Hammersmith";
            case "West Middlesex Hospital" -> "Westmid";
            case "Isleworth, West Middlesex Hospital" -> "Isleworth, Westmid";
            case "Hounslow, Bell Corner" -> "Hns, Bell Corner";
            case "Richmond, Manor Circus" -> "Richmond";
            case "Example for Testing" -> "Test Destination Name";
            default -> destination;
        };
    }

    public static String getStopNameFromId(String stopId) {
        return switch (stopId) {
            case "490009538N" -> "MDV Road > Brentford";
            case "490009538S" -> "MDV Road > Twickenham";
            case "490007338JC" -> "Grainger Road";
            case "490008512TH" -> "Isleworth War Memorial";
            case "TestStop" -> "Test Stop Name";
            default -> "Unknown";
        };
    }

}
