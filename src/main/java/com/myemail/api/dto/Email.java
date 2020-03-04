package com.myemail.api.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Email {

    private String to;
    private String from;
    private String subject;
    private String content;
    private Boolean isHtmlContent;
    private List<String> attachmentUriList;

    @Override
    public String toString() {
        return "Email{" +
                "to='" + to + '\'' +
                ", from='" + from + '\'' +
                ", subject='" + subject + '\'' +
                ", content='" + content + '\'' +
                ", attachmentUriList='" + attachmentUriList + '\'' +
                '}';
    }
}
