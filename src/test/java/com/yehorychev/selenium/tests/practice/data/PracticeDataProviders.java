package com.yehorychev.selenium.tests.practice.data;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yehorychev.selenium.helpers.JsonDataHelper;
import org.testng.annotations.DataProvider;

import java.util.List;

public final class PracticeDataProviders {

    private PracticeDataProviders() {
    }

    @DataProvider(name = "practiceAlerts")
    public static Object[][] practiceAlerts() {
        List<PracticeAlertData> records = JsonDataHelper.readResource(
                "assets/data/practice-alerts.json",
                new TypeReference<List<PracticeAlertData>>() {}
        );
        return records.stream()
                .map(data -> new Object[]{data})
                .toArray(Object[][]::new);
    }

    public record PracticeAlertData(String name, String expectedAlert) {
    }
}
