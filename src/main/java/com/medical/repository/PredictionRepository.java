 package com.medical.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.medical.entity.Prediction;

public interface PredictionRepository extends JpaRepository<Prediction, Long> {

}