package com.example.service.vet;

import com.example.service.adoptions.DogAdoptionEvent;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

@Service
class Dogtor {
     
    // spring modulith
    @ApplicationModuleListener
    void checkup(DogAdoptionEvent dogId) throws Exception {
        System.out.println("start: checking on the health for " + dogId.dogId());
        Thread.sleep(5_000);
        System.out.println("end: checking on the health for " + dogId.dogId());
    }
}
