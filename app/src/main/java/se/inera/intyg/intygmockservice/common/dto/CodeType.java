package se.inera.intyg.intygmockservice.common.dto;

import lombok.Data;

@Data
public class CodeType {

    private String code;
    private String codeSystem;
    private String displayName;
}