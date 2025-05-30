package com.paulotech.aiservice.service;

import com.paulotech.aiservice.entity.Activity;
import com.paulotech.aiservice.entity.Recommendation;
import com.paulotech.aiservice.repository.RecommendationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityMessageListener {

    private final ActivityAiService activityAiService;
    private final RecommendationRepository recommendationRepository;

    @RabbitListener(queues = "activity.queue")
    public void processActivity(Activity activity){
        log.info("Received activity for processing: {}", activity.getId());
        //log.info("Generated Recommendation: {}", activityAiService.generateRecommendation(activity));
        Recommendation recommendation = activityAiService.generateRecommendation(activity);
        recommendationRepository.save(recommendation);
    }
}
