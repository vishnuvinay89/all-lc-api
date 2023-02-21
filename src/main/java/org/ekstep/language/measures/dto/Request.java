package org.ekstep.language.measures.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Request implements Serializable {

    private static final long serialVersionUID = -2362783406031347676L;

    protected Map<String, Object> context;
    private String id;
    private String ver;
    private String ts;
    private RequestParams params;

    private Map<String, Object> request = new HashMap<String, Object>();

    private String managerName;
    private String operation;
    private String request_id;

    public String get(String key) {
        Object obj = request.get(key);
        return null != obj ? obj.toString() : null;
    }
}
