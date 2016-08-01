package com.places;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import org.hibernate.annotations.Type;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PrePersist;


@Entity
public class Place {

    @Id
    @GeneratedValue
    @JsonIgnore
    private Long id;

    private Double lat;

    private Double lng;

    private String name;

    private Double ratingTotal;

    private Long ratingCount;

    private Double rating;


    @Type(type="org.hibernate.spatial.GeometryType")
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
