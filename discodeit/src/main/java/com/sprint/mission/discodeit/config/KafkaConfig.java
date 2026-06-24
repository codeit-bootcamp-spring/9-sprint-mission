package com.sprint.mission.discodeit.config;

import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

@EnableKafka
@Configuration
public class KafkaConfig {

  /**
   * 웹소켓/SSE 팬아웃 전용 컨슈머 팩토리.
   * 인스턴스별 랜덤 그룹 ID로 매번 새 그룹이 생성되므로, 기존에 쌓인 이벤트를 재생하지
   * 않도록 latest로 시작한다 (discodeit.kafka.instance-group-id 참고).
   */
  @Bean
  public ConsumerFactory<String, String> fanoutConsumerFactory(KafkaProperties kafkaProperties) {
    Map<String, Object> props = kafkaProperties.buildConsumerProperties(null);
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
    return new DefaultKafkaConsumerFactory<>(props);
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, String> fanoutKafkaListenerContainerFactory(
      ConsumerFactory<String, String> fanoutConsumerFactory) {
    ConcurrentKafkaListenerContainerFactory<String, String> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(fanoutConsumerFactory);
    return factory;
  }
}