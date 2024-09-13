package com.example.service.adoptions;

import com.example.service.adoptions.validation.Validation;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.annotation.Id;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Transactional
@Controller
@ResponseBody
class DogAdoptionController {

    private final Validation validation;

    private final DogRepository repository;
    private final ApplicationEventPublisher publisher;

    DogAdoptionController(Validation validation, DogRepository repository, ApplicationEventPublisher publisher) {
        this.validation = validation;
        this.repository = repository;
        this.publisher = publisher;
    }

    @PostMapping("/dogs/{dogId}/adoptions")
    void adopt(@PathVariable int dogId,
               @RequestBody Map<String, String> request) {
        var name = request.get("name");
        this.repository.findById(dogId).ifPresent(dog -> {
            var newDog = this.repository.save(new Dog(dogId, name, dog.description(), name));
            System.out.println("adopted [" + newDog + "]");
            this.publisher.publishEvent(new DogAdoptionEvent(dogId));
        });

    }
}

interface DogRepository extends ListCrudRepository<Dog, Integer> {
}

// look mom, no LOMBOK!!
record Dog(@Id Integer id, String name, String description, String owner) {
}

