package com.yehorychev.selenium.data;

import java.util.List;

public record GreenKartTestData(List<String> vegetables,
                               List<String> expectedCartNames,
                               String promoCode,
                               boolean expectSorted) {
}
