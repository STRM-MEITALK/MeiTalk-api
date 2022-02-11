package com.meitalk.api.model.stream.enums;

import com.meitalk.api.exception.ResponseCodeEnum;
import com.meitalk.api.exception.StreamException;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

import java.util.stream.Stream;

@Getter
public enum CategoryType {

    FILM_ANIMATION("Film & Animation", 1),
    AUTOS_VEHICLES("Autos & Vehicles", 2),
    MUSIC("Music", 3),
    PET_ANIMALS("Pets & Animals", 4),
    SPORTS("Sports", 5),
    TRAVEL_EVENTS("Travel & Events", 6),
    GAMING("Gaming", 7),
    PEOPLE_BLOGS("People & Blogs", 8),
    COMEDY("Comedy", 9),
    ENTERTAINMENT("Entertainment", 10),
    NEWS_POLITICS("News & Politics", 11),
    HOWTO_STYLE("Howto & Style", 12),
    EDUCATION("Education", 13),
    SCIENCE_TECHNOLOGY("Science & Technology", 14),
    NONPROFITS_ACTIVISM("Nonprofits & Activism", 15);

    private final String value;
    private final int num;

    CategoryType(String value, int num) {
        this.value = value;
        this.num = num;
    }

    @JsonCreator
    public static CategoryType from(int n) {
        return Stream.of(CategoryType.values())
                .filter(v -> v.num == n)
                .findFirst()
                .orElseThrow(() -> new StreamException(ResponseCodeEnum.INVALID_PARAMETER, "Category is not valid"));
    }
}
