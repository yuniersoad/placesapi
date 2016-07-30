package com.places;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/places")
public class PlacesController {

    @Autowired
    private PlaceService service;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> add(@RequestBody Place place){
        Place p = service.addOrAgregate(place);
        return ResponseEntity.ok(p);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> find(@RequestParam Double lat, @RequestParam Double lng){

        return ResponseEntity.ok(service.findNear(lat, lng, 10.0));
    }
}
