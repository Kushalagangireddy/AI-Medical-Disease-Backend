 package com.medical.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.medical.entity.Prediction;
import com.medical.repository.PredictionRepository;

@Service
public class PredictionService {

    @Autowired
    private PredictionRepository predictionRepository;

    private final RestTemplate restTemplate = new RestTemplate();

    public Prediction predictDisease(String symptoms) {

        // Python ML API
        String mlApiUrl = "http://localhost:5000/predict";

        List<String> symptomList = List.of(
                symptoms.toLowerCase().split(",")
        );

        symptomList = symptomList.stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        Map<String, Object> requestBody = Map.of(
                "symptoms", symptomList
        );

        // Call Python ML API
        ResponseEntity<Map> response =
                restTemplate.postForEntity(
                        mlApiUrl,
                        requestBody,
                        Map.class
                );

        Map responseData = response.getBody();

        if (responseData == null) {
            throw new RuntimeException("ML API returned no response");
        }

        String disease = String.valueOf(
                responseData.get("disease")
        );

        String confidence = String.valueOf(
                responseData.get("confidence")
        ) + "%";

        // Get precautions
        String precautions = getPrecautions(disease);

        // Save prediction
        Prediction prediction = new Prediction();

        prediction.setSymptoms(symptoms);
        prediction.setDisease(disease);
        prediction.setConfidence(confidence);
        prediction.setPrecautions(precautions);

        return predictionRepository.save(prediction);
    }

    private String getPrecautions(String disease) {

        return switch (disease.toLowerCase().trim()) {

            case "drug reaction" ->
                    "Stop irritation | Consult nearest hospital | "
                    + "Stop taking drug | Follow up";

            case "malaria" ->
                    "Consult nearest hospital | Avoid oily food | "
                    + "Avoid non veg food | Keep mosquitos out";

            case "allergy" ->
                    "Apply calamine | Cover area with bandage | "
                    + "Use ice to compress itching";

            case "hypothyroidism" ->
                    "Reduce stress | Exercise | Eat healthy | "
                    + "Get proper sleep";

            case "psoriasis" ->
                    "Wash hands with warm soapy water | "
                    + "Stop bleeding using pressure | Consult doctor | "
                    + "Salt baths";

            case "gerd" ->
                    "Avoid fatty spicy food | "
                    + "Avoid lying down after eating | "
                    + "Maintain healthy weight | Exercise";

            case "chronic cholestasis" ->
                    "Cold baths | Anti itch medicine | "
                    + "Consult doctor | Eat healthy";

            case "hepatitis a" ->
                    "Consult nearest hospital | Wash hands thoroughly | "
                    + "Avoid fatty spicy food | Medication";

            case "fungal infection" ->
                    "Keep affected area clean and dry | "
                    + "Avoid scratching the affected area | "
                    + "Maintain good personal hygiene | "
                    + "Consult a doctor if symptoms persist";

            default ->
                    "Maintain good hygiene | Eat healthy food | "
                    + "Stay hydrated | Consult a doctor if symptoms persist";
        };
    }

    // Get prediction history
    public List<Prediction> getPredictionHistory() {
        return predictionRepository.findAll();
    }

    // Get total predictions
    public long getTotalPredictions() {
        return predictionRepository.count();
    }

    // Get latest prediction
    public Prediction getLatestPrediction() {

        List<Prediction> predictions =
                predictionRepository.findAll();

        if (predictions.isEmpty()) {
            return null;
        }

        return predictions.get(predictions.size() - 1);
    }
}