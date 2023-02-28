package com.bindord.eureka.gateway.services;

import com.bindord.eureka.resourceserver.model.Specialist;
import com.bindord.eureka.auth.model.SpecialistPersist;
import reactor.core.publisher.Mono;

public interface SpecialistService {

    Mono<Specialist> update(SpecialistPersist specialist);
}
