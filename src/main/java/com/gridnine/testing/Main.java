package com.gridnine.testing;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        var flights = FlightBuilder.createFlights();
        var beforeCurrentTimeFlights = extractFlightsBeforeCurrentTime(flights);
        var corruptedSegmentsFlights = extractCorruptedSegmentsFlights(flights);
        var groundTimeTwoHoursFlights = extractGroundTimeGtTwoHoursFlights(flights);

        System.out.println("Flights: " + flights);
        System.out.println("Flights before current time: " + beforeCurrentTimeFlights);
        System.out.println("Flights with corrupted segments: " + corruptedSegmentsFlights);
        System.out.println("Flights with spent ground time greater then 2 hours: " + groundTimeTwoHoursFlights);
    }

    private static List<Flight> extractFlightsBeforeCurrentTime(List<Flight> flights) {
        Filter<Flight> filter = generateFlightsBeforeCurrentTimeFilter();
        return filter.apply(flights);
    }

    private static List<Flight> extractCorruptedSegmentsFlights(List<Flight> flights) {
        Filter<Flight> filter = generateCorruptedSegmentsFlightsFilter();
        return filter.apply(flights);
    }

    private static List<Flight> extractGroundTimeGtTwoHoursFlights(List<Flight> flights) {
        Filter<Flight> filter = generateGroundTimeGtTwoHoursFlightsFilter();
        return filter.apply(flights);
    }

    private static Filter<Flight> generateFlightsBeforeCurrentTimeFilter() {
        return Filter.<Flight>builder(flight -> flight.getSegments().stream()
                .anyMatch(segment -> segment.getDepartureDate().isBefore(LocalDateTime.now())))
                .build();
    }

    private static Filter<Flight> generateCorruptedSegmentsFlightsFilter() {
        return Filter.<Flight>builder(flight -> flight.getSegments().stream()
                .anyMatch(segment -> segment.getArrivalDate().isBefore(segment.getDepartureDate())))
                .build();
    }

    private static Filter<Flight> generateGroundTimeGtTwoHoursFlightsFilter() {
        return Filter.<Flight>builder(flight -> {
                var segments = flight.getSegments();
                long groundSeconds = 0;

                    for (int i = 1; i < flight.getSegments().size(); ++i) {
                        groundSeconds +=
                                segments.get(i).getDepartureDate().toEpochSecond(ZoneOffset.UTC) -
                                        segments.get(i-1).getArrivalDate().toEpochSecond(ZoneOffset.UTC);

                }

                return TimeUnit.HOURS.toSeconds(2) < groundSeconds;
        })
                .build();
    }
}
