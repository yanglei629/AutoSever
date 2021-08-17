package bean;

import com.alibaba.fastjson.annotation.JSONField;

public class Config {
    @JSONField(ordinal = 0)
    public String equipmentName;
    @JSONField(ordinal = 1)
    public String modelName;
    @JSONField(ordinal = 2)
    public String workPath;
    @JSONField(ordinal = 3)
    public String mqAddress;
    @JSONField(ordinal = 4)
    public String devMode;
}
