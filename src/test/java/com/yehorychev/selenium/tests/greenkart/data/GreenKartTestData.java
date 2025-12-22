package com.yehorychev.selenium.tests.greenkart.data;

import java.util.List;

public record GreenKartTestData(List<String> vegetables,
                                List<String> expectedCartNames,
                                String promoCode,
                                boolean expectSorted) {
}
