package com.places;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    private Float rating;

    /*@Type(type="org.hibernate.spatial.GeometryType")
    private Point point;*/


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

    public Float getRating() {
        return rating;
    }

    /*@PrePersist
    public void prePersist(){
        point = (new GeometryFactory()).createPoint(new Coordinate(lat, lng));
    }*/

    public Place(String name, Double lat, Double lng, Float rating){
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.rating = rating;
    }

    Place(){

    }

    public void setRating(Float rating) {
        this.rating = rating;
    }
}
