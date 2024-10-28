package dev.jarradclark.api.TFLProxySpring.services.model;

public class ArrivalComparator implements java.util.Comparator<Arrival> {
    @Override
    public int compare(Arrival o1, Arrival o2) {
        return o1.getTimeToStation() - o2.getTimeToStation();
    }
}
