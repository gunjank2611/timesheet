package com.example.dto;

import com.google.common.collect.BiMap;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class MapUtility {
    public String getBloburl() {
        return bloburl;
    }

    public void setBloburl(String bloburl) {
        this.bloburl = bloburl;
    }

    private Map<String, List<String>> nagarroMap = null;
    private BiMap<String, String> proWandEmpMap = null;
    private String bloburl = null;

    public Map<String, List<String>> getNagarroMap() {
        return nagarroMap;
    }

    public void setNagarroMap(Map<String, List<String>> nagarroMap) {
        this.nagarroMap = nagarroMap;
    }

    public BiMap<String, String> getProWandEmpMap() {
        return proWandEmpMap;
    }

    public void setProWandEmpMap(BiMap<String, String> proWandEmpMap) {
        this.proWandEmpMap = proWandEmpMap;
    }

}
