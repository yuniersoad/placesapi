package com.places;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import com.wantedtech.common.xpresso.x;

import java.util.List;
import java.util.Optional;

@Primary
@Component
public class PlaceServiceImpl implements PlaceService {

    @Autowired
    private PlaceRepository repo;

    @Value("${places.name.similarity.threshold}")
    private Integer similarityThreshold;

    @Value("${places.near.radio}")
    private Double nearRadio;

    @Override
    public Place addOrAgregate(Place place){
        final String name = place.getName();
        List<Place> nearPlaces = findNear(place.getLat(), place.getLng());
        Optional<Place> match = nearPlaces.stream().filter((p -> fuzzyMacht(name, p.getName()))).findFirst();

        Place result =  match.map(
                p -> mergePlaces(p, place)
                ).orElse(place);
        repo.save(result);
        return result;
    }

    @Override
    public List<Place> findNear(Double lat, Double lng, Double radio) {
        return repo.findNear(lat, lng, radio);
    }

    @Override
    public List<Place> findNear(Double lat, Double lng) {
        return repo.findNear(lat, lng, nearRadio);
    }

    private Place mergePlaces(Place p, Place place) {
        p.addRating(place.getRating());
        return  p;
    }

    private boolean fuzzyMacht(String s1, String s2) {
        int similarity = x.String(s1).similarity(s2);
        return   similarity >= similarityThreshold;
    }


}
