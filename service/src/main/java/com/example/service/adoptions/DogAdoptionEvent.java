package com.example.service.adoptions;

//@Externalized(target = "destination-in-rabbitmq::routing-key")
public record DogAdoptionEvent(int dogId) {
}
