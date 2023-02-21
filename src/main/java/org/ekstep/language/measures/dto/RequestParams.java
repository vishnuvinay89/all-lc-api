package org.ekstep.language.measures.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestParams implements Serializable {

    private static final long serialVersionUID = -759588115950763188L;

    private String did;
    private String key;
    private String msgid;
    private String uid;
    private String cid;
    private String sid;
}
