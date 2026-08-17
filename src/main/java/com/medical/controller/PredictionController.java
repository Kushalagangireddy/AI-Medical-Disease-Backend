 package com.medical.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.medical.entity.Prediction;
import com.medical.service.PredictionService;

@RestController
@RequestMapping("/prediction")
@CrossOrigin(origins = "http://localhost:5173")
public class PredictionController {

    @Autowired
    private PredictionService predictionService;

    // Predict disease
    @PostMapping("/predict")
    public Prediction predictDisease(@RequestBody Prediction prediction) {

        return predictionService.predictDisease(
                prediction.getSymptoms()
        );
    }

    // Get prediction history
    @GetMapping("/history")
    public List<Prediction> getPredictionHistory() {

        return predictionService.getPredictionHistory();
    }

    // Get total predictions
    @GetMapping("/total")
    public long getTotalPredictions() {

        return predictionService.getTotalPredictions();
    }

    // Get latest prediction
    @GetMapping("/latest")
    public Prediction getLatestPrediction() {

        return predictionService.getLatestPrediction();
    }
}