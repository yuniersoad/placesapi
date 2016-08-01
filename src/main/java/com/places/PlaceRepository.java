package com.places;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PlaceRepository extends JpaRepository<Place, Long> {

    @Query("select p from Place p where distance(p.point, ST_MakePoint(?1, ?2)) <= ?3")
    public List<Place> findNear(Double lat, Double lng, Double radio);

}
