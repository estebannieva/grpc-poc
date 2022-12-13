package dev.estebannieva.categories.client;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class TaskRestApiClientImp implements TaskRestApiClient {
    
    private final RestTemplate restTemplate;

    public TaskRestApiClientImp(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public long countByCategoryId(String categoryId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity requestEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Long> response = restTemplate.exchange("http://localhost:8081/api/v1/tasks/count?categoryId={id}", HttpMethod.GET, requestEntity, Long.class, categoryId);
            return response.getBody();
        } catch (RestClientException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
