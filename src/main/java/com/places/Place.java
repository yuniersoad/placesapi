package com.places;

import com.fasterxml.jackson.annotation.JsonIgnore;


import org.hibernate.validator.constraints.Range;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.validation.constraints.Size;


@Entity
public class Place {
    @Id
    @GeneratedValue
    @JsonIgnore
    private Long id;

    @Range(min = -90, max = 90)
    private Double lat;

    @Range(min = -180, max = 180)
    private Double lng;

    @Size(min=2, max=30)
    private String name;

    private Double ratingTotal;

    private Long ratingCount;

    @Range(min = 1, max = 5)
    private Double rating;



    private Point point;


    public Long getId() {
        return id;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLng() {
        return lng;
    }

    public String getName() {
        return name;
    }

    public Double getRating() {
        return rating;
    }

    @PrePersist
    public void prePersist(){
        point = (new GeometryFactory()).createPoint(new Coordinate(lat, lng));
        if (id == null){
            ratingTotal = rating;
            ratingCount = 1L;
        }
    }

    public Place(String name, Double lat, Double lng, Double rating){
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.point = (new GeometryFactory()).createPoint(new Coordinate(lat, lng));
        this.rating = rating;
        this.ratingTotal = 0.0;
        this.ratingCount = 0L;
    }

    Place(){
        this.ratingTotal = 0.0;
        this.ratingCount = 0L;
    }

    public void addRating(Double rating) {
        ratingTotal += rating;
        ratingCount++;
        this.rating = ratingTotal / ratingCount;
    }
}
