package com.example.energyapi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
		"spring.datasource.url=jdbc:h2:mem:energyapi-test;DB_CLOSE_DELAY=-1",
		"spring.datasource.driver-class-name=org.h2.Driver",
		"spring.jpa.hibernate.ddl-auto=create-drop",
		"spring.rabbitmq.listener.simple.auto-startup=false",
		"spring.rabbitmq.listener.direct.auto-startup=false"
})
class EnergyapiApplicationTests {

	@Autowired
	private EnergyMessageListener listener;

	@Autowired
	private EnergyService energyService;

	@Autowired
	private EnergyMeasurementRepository repository;

	@BeforeEach
	void resetDatabase() {
		repository.deleteAll();
	}

	@Test
	void contextLoads() {
	}

	@Test
	void listenerPersistsMessagesAndApiReadsAggregatesFromDatabase() {
		listener.receive(message("PRODUCER", 1.5, "2026-06-10T14:05:00"));
		listener.receive(message("USER", 2.0, "2026-06-10T14:10:00"));

		List<HistoricalEnergy> historicalEnergy = energyService.getHistoricalEnergy(
				java.time.LocalDateTime.parse("2026-06-10T14:00:00"),
				java.time.LocalDateTime.parse("2026-06-10T15:00:00")
		);
		CurrentEnergy currentEnergy = energyService.getCurrentEnergy();

		assertThat(repository.count()).isEqualTo(2);
		assertThat(historicalEnergy).hasSize(1);
		assertThat(historicalEnergy.getFirst().communityProduced).isEqualTo(1.5);
		assertThat(historicalEnergy.getFirst().communityUsed).isEqualTo(2.0);
		assertThat(historicalEnergy.getFirst().gridUsed).isEqualTo(0.5);
		assertThat(currentEnergy.hour).isEqualTo("2026-06-10T14:00");
		assertThat(currentEnergy.communityDepleted).isEqualTo(1.5);
		assertThat(currentEnergy.gridPortion).isEqualTo(0.5);
	}

	private EnergyMessage message(String type, double kwh, String datetime) {
		EnergyMessage message = new EnergyMessage();
		message.setType(type);
		message.setAssociation("COMMUNITY");
		message.setKwh(kwh);
		message.setDatetime(datetime);
		return message;
	}
}
