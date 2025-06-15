package com.example.demoSWP.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestResultDTO {
    private Long testResultId;
    private Long customerId;
    private Long doctorId;
    private String doctorName;
    private String date;
    private String typeOfTest;
    private String resultDescription;
    private String customerName;
    private String customerEmail;

}
