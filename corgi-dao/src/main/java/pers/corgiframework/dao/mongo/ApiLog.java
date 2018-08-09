package pers.corgiframework.dao.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * Created by syk on 2018/8/9.
 */
@Document(collection = "api_log")
public class ApiLog {
    @Id
    private String id;
    @Indexed()
    private Integer userId;
    @Indexed
    private String mobile;
    @Indexed
    private String serviceId;
    private String paramIn;
    private String paramOut;
    private Integer execTime;
    private Integer logSource;
    private Integer status;
    private LocalDateTime createTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getParamIn() {
        return paramIn;
    }

    public void setParamIn(String paramIn) {
        this.paramIn = paramIn;
    }

    public String getParamOut() {
        return paramOut;
    }

    public void setParamOut(String paramOut) {
        this.paramOut = paramOut;
    }

    public Integer getExecTime() {
        return execTime;
    }

    public void setExecTime(Integer execTime) {
        this.execTime = execTime;
    }

    public Integer getLogSource() {
        return logSource;
    }

    public void setLogSource(Integer logSource) {
        this.logSource = logSource;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
