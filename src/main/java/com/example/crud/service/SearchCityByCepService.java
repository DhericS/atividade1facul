package com.example.crud.service;

import com.example.crud.domain.product.Product;
import com.example.crud.domain.product.ProductRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class SearchCityByCepService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final ProductRepository productRepository;

    @Autowired
    public SearchCityByCepService(ProductRepository productRepository) {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
        this.productRepository = productRepository;
    }

    public Boolean SearchCityOfProductByCep(String id, String cep) {
        try {
            Optional<Product> optionalProduct = productRepository.findById(id);
            if (optionalProduct.isEmpty()) {
                return false;
            }

            Product product = optionalProduct.get();

            String url = "https://viacep.com.br/ws/{cep}/json/";
            Map<String, String> uriVariables = new HashMap<>();
            uriVariables.put("cep", cep);

            String resposta = restTemplate.getForObject(url, String.class, uriVariables);
            JsonNode json = objectMapper.readTree(resposta);
            String cidadeViaCep = json.get("localidade").asText();

            return cidadeViaCep.equalsIgnoreCase(product.getDistributionCenter());

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
