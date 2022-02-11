package com.meitalk.api.model.stream;

import lombok.*;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikeResponse {
    private Long commentId;
    private Long userNo;
    private Long typeId;
    private String type;
    private Long likeCount;
    private boolean likeToggle;
}
