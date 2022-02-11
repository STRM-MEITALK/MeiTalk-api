package com.meitalk.api.model.comment;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class CommentRequest {
    private Long contentId;
    private String content;
    private String type;
}
