package com.bindord.eureka.gateway.services.impl;

//import com.bindord.eureka.auth.model.SpecialistPersist;
import com.bindord.eureka.gateway.advice.CustomValidationException;
import com.bindord.eureka.gateway.services.SpecialistService;
import com.bindord.eureka.gateway.wsc.ResourceServerClientConfiguration;
import com.bindord.eureka.resourceserver.model.*;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.BeanUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import java.util.UUID;

@Service
@AllArgsConstructor
public class SpecialistServiceImpl implements SpecialistService {

    private final ResourceServerClientConfiguration resourceServerClientConfiguration;

    @Override
    public Mono<Specialist> update(com.bindord.eureka.auth.model.SpecialistPersist specialist) {
        return this.doValidateIfSpecialistExits(specialist.getId()).flatMap(exist -> {
                    if(!exist){
                        return Mono.error(new CustomValidationException("Specialist not found"));
                    }
                    return Mono.empty();
                })
                .then(
                    Mono.zip(doUpdateSpecialist(specialist), doUpdateSpecialistCv(specialist))
                            .flatMap(t -> Mono.just(t.getT1()))
                );
    }

    @SneakyThrows
    private Mono<Boolean> doValidateIfSpecialistExits(UUID id){
        if(id == null){
            return Mono.just(false);
        }

        return resourceServerClientConfiguration.init()
                .get()
                .uri(uriBuilder ->  uriBuilder.path("/specialist/{id}")
                        .build(id))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Boolean.class);
    }

    @SneakyThrows
    private Mono<Specialist> doUpdateSpecialist(com.bindord.eureka.auth.model.SpecialistPersist specialist){
        var specialistDto = convertSpecialistToDTO(specialist);

        return resourceServerClientConfiguration.init()
                .put()
                .uri("/specialist")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(specialistDto, SpecialistUpdateDto.class)
                .retrieve()
                .bodyToMono(Specialist.class).log();
    }

    @SneakyThrows
    private Mono<SpecialistCv> doUpdateSpecialistCv(com.bindord.eureka.auth.model.SpecialistPersist specialist){
        var specialistCVDto = convertSpecialistCVToDTO(specialist);

        return resourceServerClientConfiguration.init()
                .put()
                .uri("/specialist-cv")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(specialistCVDto, SpecialistCvDto.class)
                .retrieve()
                .bodyToMono(SpecialistCv.class);

    }

    private Mono<SpecialistUpdateDto> convertSpecialistToDTO(com.bindord.eureka.auth.model.SpecialistPersist specialist){
        SpecialistUpdateDto specialistDto = new SpecialistUpdateDto();
        BeanUtils.copyProperties(specialist, specialistDto);

        return Mono.just(specialistDto);
    }

    private Mono<SpecialistCvDto> convertSpecialistCVToDTO(com.bindord.eureka.auth.model.SpecialistPersist specialist){
        SpecialistCvDto specialistCvDto = new SpecialistCvDto();
        BeanUtils.copyProperties(specialist, specialistCvDto);

        return Mono.just(specialistCvDto);
    }
}
