package com.bindord.eureka.gateway.controller.auth;

import com.bindord.eureka.auth.model.Specialist;
import com.bindord.eureka.auth.model.SpecialistPersist;
import com.bindord.eureka.gateway.wsc.AuthClientConfiguration;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.validation.Valid;

@AllArgsConstructor
@RestController
@RequestMapping("${service.ingress.context-path}/specialist")
@Slf4j
public class SpecialistController {

    private final AuthClientConfiguration authClientConfiguration;

    @ApiResponse(description = "Persist a specialist",
            responseCode = "200")
    @PostMapping(value = "",
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public Mono<Specialist> persistSpecialist(@Valid @RequestBody SpecialistPersist specialist) throws InterruptedException {
        return authClientConfiguration.init()
                .post()
                .uri("/specialist")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(specialist), SpecialistPersist.class)
                .retrieve()
                .bodyToMono(Specialist.class)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @ApiResponse(description = "Only test",
            responseCode = "200")
    @GetMapping(value = "/test/check",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public Mono<String> onlyTest() throws InterruptedException {
        return authClientConfiguration.init()
                .get()
                .uri("/user-accounts/865db1ff-7a12-409b-8ec4-763df49d980e/citizenship/documents")
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICIwNlNKZjRESTlXaDRkZEdEajZGTTBXa0hlYjdvbEpDZ3BUbTBjbUxfTUswIn0.eyJleHAiOjE2NjQ0MzM3MTYsImlhdCI6MTY2NDQzMzQxNiwiYXV0aF90aW1lIjoxNjY0NDI4NjQzLCJqdGkiOiIwZGYyMTBjMi01OTgyLTRhYWUtYTc0MS1hMjI0NDNlMTQwNjciLCJpc3MiOiJodHRwczovL2lkZW50aWRhZC1zYW5kYm94LmRpZ2l0YWwub3RpYy5wZS9hdXRoL3JlYWxtcy9kZXYtcGUtd2ViIiwiYXVkIjpbInBlLndlYi1zZXJ2aWNlcy51c2VyLmFjY291bnRzIiwicGUud2ViLXNlcnZpY2VzLmVkdWNhdGlvbmFsLWluc3RpdHV0aW9ucyIsImFjY291bnQiXSwic3ViIjoiODY1ZGIxZmYtN2ExMi00MDliLThlYzQtNzYzZGY0OWQ5ODBlIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoicGUud2ViLWFwcC5zY2hvb2wtc3BvcnRzIiwibm9uY2UiOiJmZGUzYmVlOS03NDRjLTQwYzItODlhOC0xNDhlY2EyZmU0ODAiLCJzZXNzaW9uX3N0YXRlIjoiOTM2MWU4OTItZTAzMy00ZWM2LWJlZTUtOWYwYzJmZjgyY2E1IiwiYWxsb3dlZC1vcmlnaW5zIjpbIioiXSwicmVzb3VyY2VfYWNjZXNzIjp7InBlLndlYi1zZXJ2aWNlcy51c2VyLmFjY291bnRzIjp7InJvbGVzIjpbInVzZXItYWNjb3VudHM6cHJvZmlsZTpyZWFkIiwidXNlci1hY2NvdW50czplZHVjYXRpb25zOnJlYWQiLCJ1c2VyLWFjY291bnRzOmNpdGl6ZW5zaGlwOnJlYWQiLCJ1c2VyLWFjY291bnRzOmVtcGxveW1lbnRzOnJlYWQiXX0sInBlLndlYi1zZXJ2aWNlcy5lZHVjYXRpb25hbC1pbnN0aXR1dGlvbnMiOnsicm9sZXMiOlsiZWR1Y2F0aW9uYWwtaW5zdGl0dXRpb25zOnJlYWQiXX0sImFjY291bnQiOnsicm9sZXMiOlsidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJvcGVuaWQgcHJvZmlsZSBlbWFpbCIsInNpZCI6IjkzNjFlODkyLWUwMzMtNGVjNi1iZWU1LTlmMGMyZmY4MmNhNSIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJuYW1lIjoiSlVBTiBDQVJMT1MgQUxJQUdBIEJBUkNBIiwicHJlZmVycmVkX3VzZXJuYW1lIjoiZG5pLjQxNzMzMzY3QHlvcG1haWwuY29tIiwiZ2l2ZW5fbmFtZSI6IkpVQU4gQ0FSTE9TIiwiZmFtaWx5X25hbWUiOiJBTElBR0EgQkFSQ0EiLCJlbWFpbCI6ImRuaS40MTczMzM2N0B5b3BtYWlsLmNvbSJ9.bVCSBBdZWUMZxlLZtQiKHPb7f6cnIFRaYOLL-vTDzP1ft-lSqjnHbyqWWlx03N9pc7BADfmc9XaN6Yzb-OKiyqsY_fmC0hQu3FZy49jvTd8mToaYjRiyo2GUKdCQ31VdGrLnYVUrzB4dGooEVtyikO_0KTGVoVomjprlM1P0aK_VR4fUkvBmVflY0YNbwLZRTRCC7Tl8BGqMpBtnP_mXb8oxDT60xjoTs8G_5CFip9MkRjz7TPizSxYENsymLAiqQE-BW5JCSA5fb_xxaJVASJbVRqlNJ1JOjtNvtL5wyB4j8rk-Rd0qQrlZ3YQ4CzjbwVYv67LQKXGAj8tCyzxg_Q")
                .retrieve()
                .bodyToMono(String.class)
                .subscribeOn(Schedulers.boundedElastic());
    }
}
