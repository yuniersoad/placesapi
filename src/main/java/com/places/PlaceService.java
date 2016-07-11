package com.places;

import java.util.List;

public interface PlaceService {
    public void addOrAgregate(Place place);

    /**
     *
     * @param lat
     * @param lng
     * @param radio in meters
     * @return Near Places
     */
    public List<Place> findNear(Double lat, Double lng, Double radio);
}
