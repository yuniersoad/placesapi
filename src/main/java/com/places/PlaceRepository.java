package com.places;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PlaceRepository extends JpaRepository<Place, Long> {

    @Query("select p from Place p where ABS(p.lat - ?1) < ?3 and ABS(p.lng - ?2) < ?3")
    public List<Place> findNear(Double lat, Double lng, Double radio);

}
